package de.renber.databinding.collections;

/**
 * Interface for methods which transform one object type into another
 * @author renber
 *
 * @param <TIn> Source type
 * @param <TOut> Target type, source type is transformed to
 */
public interface ItemTransformer<TIn, TOut> {

	public TOut transform(TIn model);	
	
}
