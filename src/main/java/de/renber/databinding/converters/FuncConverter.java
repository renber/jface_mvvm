package de.renber.databinding.converters;

import java.util.function.Function;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * Converter which uses a functional interface to convert a provided value
 * @author renber
 */
public class FuncConverter<TValue, TResult> implements IConverter {

	Class<? extends TValue> fromClass;
	Class<? extends TResult> toClass;
	Function<TValue, TResult> func;
	
	public static <T> FuncConverter<T, T> create(Class<? extends T> clazz, Function<T, T> func) {
		return new FuncConverter<T, T>(clazz, clazz, func);
	}
	
	public static <TIn, TOut> FuncConverter<TIn, TOut> create(Class<? extends TIn> fromClass, Class<? extends TOut> toClass, Function<TIn, TOut> func) {
		return new FuncConverter<TIn, TOut>(fromClass, toClass, func);
	}	
	
	private FuncConverter(Class<? extends TValue> fromClass, Class<? extends TResult> toClass, Function<TValue, TResult> func) {
		this.fromClass = fromClass;
		this.toClass = toClass;
		this.func = func;
	}
	
	@Override
	public Object convert(Object value) {
		return func.apply((TValue)value);
	}

	@Override
	public Object getFromType() {
		return fromClass;
	}

	@Override
	public Object getToType() {
		return toClass;
	}

}
