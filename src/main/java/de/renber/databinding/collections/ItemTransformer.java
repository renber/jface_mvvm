package de.renber.databinding.collections;

public interface ItemTransformer<TModel, TViewModel> {

	public TViewModel transform(TModel model);	
	
}
