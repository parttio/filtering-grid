package org.vaadin.addons.filteringgrid;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.EnumSet;

import org.apache.commons.lang3.StringUtils;
import org.vaadin.addons.filteringgrid.FilterGrid.Column;
import org.vaadin.addons.filteringgrid.comparators.StringComparator;
import org.vaadin.addons.filteringgrid.components.DateRangeField;
import org.vaadin.addons.filteringgrid.components.NumberFilterPopup;
import org.vaadin.addons.filteringgrid.components.NumberInterval;
import org.vaadin.addons.filteringgrid.components.DateRangeField.DateRange;

import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.SerializableBiPredicate;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
/**
 * Based on FilterTable's filter generator by Teppo Kurki
 * 
 * @author sergey-vaadin
 */
public class FilterFieldGenerator implements Serializable {
    private final FilterGrid<?> owner;

    public FilterFieldGenerator(FilterGrid<?> owner) {
        this.owner = owner;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void initializeFilterFields() {
        owner.getColumns().forEach(col -> createFilter((Column)col) );
    }

  
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private boolean generateNumberFilter(NumberInterval interval,
            Object value) {
        if (interval == null) {
            /* Number interval is empty -> no filter */
            return true;
        }
        if (value == null || !(value instanceof Comparable) ) return false;
        
        Comparable comparable = (Comparable) value;

        String ltValue = interval.getLessThanValue();
        String gtValue = interval.getGreaterThanValue();
        String eqValue = interval.getEqualsValue();
        Class<?> typeClass = value.getClass();

        if (StringUtils.isNotBlank(eqValue)) {
            return comparable.equals(parseNumberValue(typeClass, eqValue));
        } else if (StringUtils.isNotBlank(ltValue) && StringUtils.isNotBlank(gtValue) ) {
            return comparable.compareTo(parseNumberValue(typeClass,ltValue)) < 0 && comparable.compareTo( parseNumberValue(
                    typeClass, gtValue)) > 0;
        } else if (StringUtils.isNotBlank(ltValue) ) {
            return comparable.compareTo(parseNumberValue(typeClass,ltValue)) < 0;
        } else if (StringUtils.isNotBlank(gtValue) ) {
            return comparable.compareTo(parseNumberValue(typeClass,gtValue)) > 0;
        }
        return false;
    }
    
    private static Comparable<?> parseNumberValue(Class<?> typeClass, String value) {
        if (typeClass == BigDecimal.class)
                return new BigDecimal(value);
        if (typeClass == BigInteger.class)
                return new BigInteger(value);
        if (typeClass == byte.class || typeClass == Byte.class)
                return Byte.valueOf(value);
        if (typeClass == short.class || typeClass == Short.class)
                return Short.valueOf(value);
        if (typeClass == int.class || typeClass == Integer.class)
                return Integer.valueOf(value);
        if (typeClass == long.class || typeClass == Long.class)
                return Long.valueOf(value);
        if (typeClass == float.class || typeClass == Float.class)
                return Float.valueOf(value);
        if (typeClass == double.class || typeClass == Double.class)
                return Double.valueOf(value);
        
        throw new UnsupportedOperationException("Unsupported number type; " + typeClass.getName());
    }

    private boolean generateDateFilter(DateRange interval, Object value) {
        /* Handle date filtering */
        if (interval == null || interval.isNull()) {
            /* Date interval is empty -> no filter */
            return true;
        }
        
        if (value == null) return false;
        
        LocalDateTime localValue = value instanceof LocalDateTime ? (LocalDateTime) value : null;
            
        
        if (value instanceof Date) {
            localValue = ((Date) value).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (value instanceof LocalDate) {
            localValue = ((LocalDate) value).atStartOfDay();
        }
        if (localValue == null ) return true; // should not happen
                
                
        if (interval.getDateFrom() != null && interval.getDateTo() != null) {
            return !localValue.isBefore(interval.getDateFrom().atStartOfDay()) && !localValue.isAfter(interval.getDateTo().plusDays(1).atStartOfDay());
        } else if (interval.getDateFrom() != null) {
            return !localValue.isBefore(interval.getDateFrom().atStartOfDay());
        } else {
            return !localValue.isAfter(interval.getDateTo().plusDays(1).atStartOfDay());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void createFilter(Column column) {
        HasValue<?> component = null;
        ValueProvider<?,?> vp = null;
        SerializableBiPredicate<?,?> predicate = null;
        
        SerializableBiPredicate<?,?> equals = (value, filterValue)  -> filterValue == null || filterValue.equals(value);
        Class<?> type = column.getValueType();
        if (owner.getFilterGenerator() != null) {
            component = (HasValue<?>) owner.getFilterGenerator().getCustomFilterComponent(column);
            vp = owner.getFilterGenerator().getValueProvider(column);
            predicate = owner.getFilterGenerator().getPredicate(column);
        }
        
        if (type == boolean.class || type == Boolean.class) {
            component = component !=null ? component :createBooleanField(column);
            predicate = predicate != null ? predicate : equals;
        } else if (type != null && type.isEnum()) {
            component = component !=null ? component :createEnumField(column); 
            predicate = predicate != null ? predicate : equals;
        } else if (type == Date.class || type == Timestamp.class || type == java.sql.Date.class
                || type == LocalDate.class || type == LocalDateTime.class) {
            component = component !=null ? component : createDateField(column);
            predicate = predicate != null ? predicate : (value, filterValue)  -> generateDateFilter ((DateRange)filterValue, value );
            
        } else if ((type == Integer.class || type == Long.class
                || type == Float.class || type == Double.class
                || type == Short.class || type == Byte.class
                || type == int.class || type == long.class
                || type == float.class || type == double.class
                || type == short.class || type == byte.class
                || type == BigDecimal.class || type == BigInteger.class)) {
            component = component !=null ? component : createNumericField(type, column);
            predicate = predicate != null ? predicate :(value, filterValue)  -> generateNumberFilter ((NumberInterval)filterValue, value);
        } else {
            component = component !=null ? component : createTextField(column);
            predicate = predicate != null ?predicate : StringComparator.containsIgnoreCase();
        }
        if (vp != null) {
            column.setFilter(vp, component, predicate);
        } else {
            column.setFilter(component, predicate);
        }
        
    }

    private TextField createTextField(Column<?,?> column) {
        final TextField textField = new TextField();
        if (owner.getFilterDecorator() != null) {
            if (owner.getFilterDecorator().isTextFilterImmediate(column)) {
                int timeout = owner.getFilterDecorator().getTextChangeTimeout(column);
                if (timeout !=0) {
                    textField.setValueChangeMode(ValueChangeMode.TIMEOUT);
                    textField.setValueChangeTimeout(timeout);
                } else {
                    textField.setValueChangeMode(ValueChangeMode.LAZY);
                }
            } else {
                textField.setValueChangeMode(ValueChangeMode.BLUR);
            }
            if (owner.getFilterDecorator().getAllItemsVisibleString() != null) {
                textField.setPlaceholder(owner.getFilterDecorator()
                        .getAllItemsVisibleString());
            }
        }
        return textField;
        
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private ComboBox createEnumField(Column column) {
        ComboBox enumSelect = new ComboBox();
        enumSelect.setEmptySelectionAllowed(true);
        if (owner.getFilterDecorator() != null
                && owner.getFilterDecorator().getAllItemsVisibleString() != null) {
            enumSelect.setEmptySelectionCaption(owner.getFilterDecorator().getAllItemsVisibleString());
        }
        enumSelect.setItems( EnumSet.allOf((Class<Enum>) column.getValueType()));
        if (owner.getFilterDecorator() != null && owner.getFilterDecorator().getEnumFilterCaptionGenerator(column) != null) {
            enumSelect.setItemCaptionGenerator(owner.getFilterDecorator().getEnumFilterCaptionGenerator(column));
        }
        if (owner.getFilterDecorator() != null && owner.getFilterDecorator().getEnumFilterIconGenerator(column) != null) {
            enumSelect.setItemIconGenerator(owner.getFilterDecorator().getEnumFilterIconGenerator(column));
        }
        return enumSelect;
    }

    private ComboBox<Boolean> createBooleanField(Column<?,?> column) {
        ComboBox<Boolean> booleanSelect = new ComboBox<>();
        booleanSelect.addStyleNames("small", "borderless");
        booleanSelect.setItems(true, false);
        booleanSelect.setEmptySelectionAllowed(true);
        if (owner.getFilterDecorator() != null) {
            // Add possible 'view all' item 
            if (owner.getFilterDecorator().getAllItemsVisibleString() != null) {
                
                booleanSelect.setEmptySelectionCaption(owner
                        .getFilterDecorator().getAllItemsVisibleString());
            }
            
            booleanSelect.setItemCaptionGenerator(e -> owner.getFilterDecorator()
                    .getBooleanFilterDisplayName(column, e));
            booleanSelect.setItemIconGenerator( e -> owner.getFilterDecorator().getBooleanFilterIcon(
                    column, e) );
        } 
        return booleanSelect;
    }

    private DateRangeField createDateField(Object propertyId) {
        DateRangeField dateField = new DateRangeField();
        dateField.setWidth(100, Unit.PERCENTAGE);
        return dateField;
    }

    private NumberFilterPopup createNumericField(Class<?> type,
            Object propertyId) {
        NumberFilterPopup numberFilterPopup = new NumberFilterPopup(
                owner.getFilterDecorator());
        numberFilterPopup.setWidth(100, Unit.PERCENTAGE);
        return numberFilterPopup;
    }
   
}
