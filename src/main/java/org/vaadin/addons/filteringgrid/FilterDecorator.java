package org.vaadin.addons.filteringgrid;

import java.io.Serializable;
import java.util.Locale;

import org.vaadin.addons.filteringgrid.FilterGrid.Column;
import org.vaadin.addons.filteringgrid.components.NumberFilterPopupConfig;

import com.vaadin.server.Resource;
import com.vaadin.ui.IconGenerator;
import com.vaadin.ui.ItemCaptionGenerator;

/**
 * Interface for decorating the UI of the filter components contained in
 * FilterGrid. Implement this interface to provide proper display names and
 * icons for enums and booleans shown in the filter components.
 * 
 * @author sergey-vaadin
 * @author Teppo Kurki
 */
public interface FilterDecorator extends Serializable {

    /**
     * Returns the caption generator for the enumeration columns
     * @param column
     * @return
     */
    public ItemCaptionGenerator<?> getEnumFilterCaptionGenerator(Column<?,?> column);

    /**
     * Returns the icon generator for the enumeration columns
     * @param column
     * @return
     */
    public IconGenerator<?> getEnumFilterIconGenerator(Column<?,?> column);

    /**
     * Returns the filter display name for the given boolean value when
     * filtering the given property id.
     * 
     * @param propertyId
     *            ID of the property the filter is attached to.
     * @param value
     *            Value of boolean the display name is requested for.
     * @return UI Display name for the given boolean value.
     */
    public String getBooleanFilterDisplayName(Column<?,?> column, boolean value);

    /**
     * Returns the filter icon for the given boolean value when filtering the
     * given property id.
     * 
     * @param propertyId
     *            ID of the property the filter is attached to.
     * @param value
     *            Value of boolean the icon is requested for.
     * @return Resource for the icon of the given boolean value.
     */
    public Resource getBooleanFilterIcon(Column<?,?> column, boolean value);

    /**
     * Returns whether the text filter should update as the user types. This
     * uses 
     * 
     * @return true if the text field should use a TextChangeListener.
     */
    public boolean isTextFilterImmediate(Column<?,?> column);

    /**
     * The text change timeout dictates how often text change events are
     * communicated to the application, and thus how often are the filter values
     * updated.
     * 
     * @return the timeout in milliseconds
     */
    public int getTextChangeTimeout(Column<?,?> column);

    /**
     * Return display caption for the From field
     * 
     * @return caption for From field
     */
    public String getFromCaption();

    /**
     * Return display caption for the To field
     * 
     * @return caption for To field
     */
    public String getToCaption();

    /**
     * Return display caption for the Set button
     * 
     * @return caption for Set button
     */
    public String getSetCaption();

    /**
     * Return display caption for the Clear button
     * 
     * @return caption for Clear button
     */
    public String getClearCaption();


    /**
     * Returns a date format pattern to be used for formatting the date/time
     * values shown in the filtering field of the given property ID. Note that
     * this is completely independent from the resolution set for the property,
     * and is used for display purposes only.
     * 
     * See SimpleDateFormat for the pattern definition
     * 
     * @param propertyId
     *            ID of the property the format will be applied to
     * @return A date format pattern or null to use the default formatting
     */
    public String getDateFormatPattern(Column<?,?> column);

    /**
     * Returns the locale to be used with Date filters. If none is provided,
     * reverts to default locale of the system.
     * 
     * @return Desired locale for the dates
     */
    public Locale getLocale();

    /**
     * Return the string that should be used as an "input prompt" when no
     * filtering is made on a filter component.
     * 
     * @return String to show for no filter defined
     */
    public String getAllItemsVisibleString();

    /**
     * Return configuration for the numeric filter field popup
     * 
     * @return Configuration for numeric filter
     */
    public NumberFilterPopupConfig getNumberFilterPopupConfig();

    /**
     * Defines whether a popup-style numeric filter should be used for the
     * property with the given ID.
     * 
     * The types Integer, Long, Float and Double are considered to be 'numeric'
     * within this context.
     * 
     * @param propertyId
     *            ID of the property the popup will be applied to
     * @return true to use popup-style, false to use a TextField
     */
    public boolean usePopupForNumericProperty(Column<?,?> column);
}
