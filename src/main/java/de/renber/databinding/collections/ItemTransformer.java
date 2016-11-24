package de.renber.databinding.collections;

/**
 * Interface for methods which transform one object type into another
 * @author renber
 *
 * @param <TIn>
 * @param <TOut>
 */
public interface ItemTransformer<TIn, TOut> {

	public TOut transform(TIn model);	
	
}
