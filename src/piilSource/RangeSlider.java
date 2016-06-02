/*  
    PiiL: Pathway Interactive vIsualization tooL
    Copyright (C) 2015  Behrooz Torabi Moghadam

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package piilSource;

import javax.swing.JSlider;

/**
 * An extension of JSlider to select a range of values using two thumb controls.
 * The thumb controls are used to select the lower and upper value of a range
 * with predetermined minimum and maximum values.
 * 
 * <p>Note that RangeSlider makes use of the default BoundedRangeModel, which 
 * supports an inner range defined by a value and an extent.  The upper value
 * returned by RangeSlider is simply the lower value plus the extent.</p>
 */
public class RangeSlider extends JSlider {

    public RangeSlider() {
        initSlider();
    }

    public RangeSlider(int min, int max) {
        super(min, max);
        initSlider();
    }

    private void initSlider() {
        setOrientation(HORIZONTAL);
    }

    public void updateUI() {
        setUI(new RangeSliderUI(this));
        updateLabelUIs();
    }

    public int getValue() {
        return super.getValue();
    }

    public void setValue(int value) {
        int oldValue = getValue();
        if (oldValue == value) {
            return;
        }

        int oldExtent = getExtent();
        int newValue = Math.min(Math.max(getMinimum(), value), oldValue + oldExtent);
        int newExtent = oldExtent + oldValue - newValue;

        getModel().setRangeProperties(newValue, newExtent, getMinimum(), 
            getMaximum(), getValueIsAdjusting());
    }

    public int getUpperValue() {
        return getValue() + getExtent();
    }

    public void setUpperValue(int value) {
        
        int lowerValue = getValue();
        int newExtent = Math.min(Math.max(0, value - lowerValue), getMaximum() - lowerValue);
        
        setExtent(newExtent);
    }
}
