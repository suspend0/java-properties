package ca.hullabaloo.properties;

import net.sf.cglib.proxy.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static ca.hullabaloo.properties.Utils.checkArgument;

/**
 * Implements a class based on a properties file using <b>cglib</b> to generate the bytecode.
 *
 * @see #create
 */
class CglibPropertyImpl<T> {
  public static <T> T create(Class<T> type, Resolver props, Converter converter) {
    validateType(type);
    return new CglibPropertyImpl<T>(type, props, converter).createInstance();
  }

  private static <T> void validateType(Class<T> type) {
    if (!Modifier.isPublic(type.getModifiers()) || Modifier.isFinal(type.getModifiers())) {
      throw JavaPropertiesException.nonPublicOrFinalClass(type);
    }
    if (type.getEnclosingClass() != null && !Modifier.isStatic(type.getModifiers())) {
      throw JavaPropertiesException.nonStaticInnerClass(type);
    }
  }

  private final Class<T> type;

  private final Resolver props;

  private final Converter converter;

  private CglibPropertyImpl(Class<T> type, Resolver props, Converter converter) {
    this.props = props;
    this.type = type;
    this.converter = converter;
  }

  private T createInstance() {
    JavaPropertiesException.Builder errors = JavaPropertiesException.builder();

    // see potentialMethods(), but basically we don't support subtypes because
    // validating that the types we construct involves walking the class tree
    // looking for methods which might override the methods we're concerned about
    // and that is super hard and better left to compiler writers.  Simple is the key.
    checkArgument(type.getSuperclass() == Object.class || type.isInterface());
    boolean constants = Constants.class.isAssignableFrom(this.type);

    // callbacks are the cglib type that implements a method's behaviour, the
    // callbackIndex is b/c the Enhancer api (see below) requires I map method
    // invocations to callbacks via an integer lookup
    List<Callback> callbacks = new ArrayList<Callback>();
    callbacks.add(NoOp.INSTANCE);
    Map<Method, Integer> callbackIndex = new HashMap<Method, Integer>();

    // this is where we setup a callback for each method
    for (Method m : potentialMethods()) {
      boolean constantValue = false;
      checkArgument(m.getParameterTypes().length == 0);

      Class<?> valueType = m.getReturnType();

      String prop = Utils.propertyName(m);
      String value = this.props.resolve(prop);
      if (value == null) {
        value = fromDefaultAnnotation(m);
        constantValue = true;
      }
      Object converted = null;
      try {
        converted = converter.convert(value, valueType);
      } catch (RuntimeException e) {
        //noinspection ThrowableResultOfMethodCallIgnored
        errors.conversionError(m, value, e);
      }
      if (converted == null) {
        // nothing; we'll check if this is OK later
      } else if (constants || constantValue) {
        callbackIndex.put(m, callbacks.size());
        callbacks.add(new FixedValueImpl(converted));
      } else {
        callbackIndex.put(m, callbacks.size());
        callbacks.add(new LiveValueImpl(valueType, converter, prop, props));
      }
    }

    // Here we create a subclass implementing the interface
    Enhancer e = new Enhancer();
    e.setUseFactory(false);
    e.setUseCache(false);
    e.setCallbacks(callbacks.toArray(new Callback[callbacks.size()]));
    e.setCallbackFilter(new MapCallbackFilter(callbackIndex));

    if (this.type.isInterface()) {
      e.setInterfaces(new Class[]{this.type});
    } else {
      e.setSuperclass(this.type);
    }

    // Construct, and then validate we don't still have any unimplemented methods
    // Instead of doing a bunch of up-front validation, we just try the mappings
    // and then check that the result is correct.  It's more reliable that way.
    Object instance = e.create();
    validateConstructedType(instance.getClass(), errors);
    errors.throwIfErrors(this.type);

    return castToType(instance);
  }

  private String fromDefaultAnnotation(Method m) {
    Default def = m.getAnnotation(Default.class);
    return def == null ? null : def.value();
  }

  /**
   * These are all of the methods which we could override.  For interfaces, we really
   * need to override them all, but some methods on abstract types may have their own
   * implementation already.
   */
  private Iterable<Method> potentialMethods() {
    // since we don't support inheritance for our types, this can simply
    // be current class + interface methods.   Note that if a class implements
    // an interface method, IT is the declaring class, but if it does not
    // (say, it's an abstract class) than the declaring class is still
    // the interface
    Set<Method> result = new LinkedHashSet<Method>();
    for (Method m : this.type.getDeclaredMethods()) {
      if (Modifier.isFinal(m.getModifiers()) || Modifier.isPrivate(m.getModifiers())) {
        continue;
      }
      if (isSupportedReturnType(m.getReturnType())) {
        result.add(m);
      }
    }
    for (Method m : this.type.getMethods()) {
      if (m.getDeclaringClass().isInterface()) {
        if (isSupportedReturnType(m.getReturnType())) {
          result.add(m);
        }
      }
    }
    return result;
  }

  private boolean isSupportedReturnType(Class<?> returnType) {
    return converter.supportsTarget(returnType);
  }

  @SuppressWarnings({"unchecked"})
  private T castToType(Object o) {
    return (T) o;
  }

  /**
   * We don't do a ton of error checking up front, so this is just a
   * quick validation to assure that no abstract methods are on the
   * type we return to the client.
   */
  private void validateConstructedType(Class constructed, JavaPropertiesException.Builder errors) {
    for (Method m : constructed.getMethods()) {
      if (Modifier.isAbstract(m.getModifiers())) {
        addError(errors, m);
      }
    }
    for (Method m : constructed.getDeclaredMethods()) {
      if (Modifier.isAbstract(m.getModifiers())) {
        addError(errors, m);
      }
    }
  }

  private void addError(JavaPropertiesException.Builder errors, Method m) {
    boolean added = false;
    if (!isSupportedReturnType(m.getReturnType())) {
      errors.unsupportedReturnType(m);
      added = true;
    }
    if (this.props.resolve(Utils.propertyName(m)) == null) {
      errors.missingProperty(Utils.propertyName(m), m);
      added = true;
    }
    if (!added) {
      errors.unknownError(m);
    }
  }

  /**
   * @see #createInstance
   */
  private static class MapCallbackFilter implements CallbackFilter {
    private final Map<Method, Integer> callbackIndexes;

    public MapCallbackFilter(Map<Method, Integer> callbackIndexes) {
      this.callbackIndexes = callbackIndexes;
    }

    public int accept(Method method) {
      Integer i = this.callbackIndexes.get(method);
      return i == null ? 0 : i;
    }
  }

  /**
   * A method implementation that returned a fixed value for a method invocation
   */
  private static class FixedValueImpl implements FixedValue {
    private final Object value;

    public FixedValueImpl(Object value) {
      this.value = value;
    }

    public Object loadObject() throws Exception {
      return this.value;
    }
  }

  /**
   * A method implementation that returns a value from map every time a method is called
   */
  private static class LiveValueImpl implements MethodInterceptor {
    private final Class<?> valueType;
    private final Converter converter;
    private final String name;
    private final Resolver props;

    public LiveValueImpl(Class<?> valueType, Converter converter, String name, Resolver props) {
      this.valueType = valueType;
      this.converter = converter;
      this.name = name;
      this.props = props;
    }

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) {
      String value = props.resolve(name);
      return converter.convert(value, valueType);
    }
  }
}
