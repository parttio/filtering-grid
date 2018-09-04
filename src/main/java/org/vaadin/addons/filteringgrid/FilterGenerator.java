package org.vaadin.addons.filteringgrid;

import java.io.Serializable;

import org.vaadin.addons.filteringgrid.FilterGrid.Column;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.SerializableBiPredicate;
import com.vaadin.ui.Component;


/**
 * Interface for generating custom Filters from values entered to the filtering
 * fields by the user.
 * 
 * @author sergey-vaadin
 */
public interface FilterGenerator extends Serializable {

    /**
     * Overrides ValueProvider for the filter
     * @param column
     * @return
     */
    ValueProvider<?,?> getValueProvider(Column<?,?> column);
    
    /**
     * Defines logical predicate for the filter
     * @param column
     * @return
     */
    public SerializableBiPredicate<?,?> getPredicate(Column<?,?> column);
   
    /**
     * Returns Vaadin component to be used as a filter
     * @param column
     * @return
     */
    public Component getCustomFilterComponent(Column<?,?> column);
  
}
