/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.phygen.core.ds.network;

import uk.ac.uea.cmp.phygen.core.ds.Taxa;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 18/11/13
 * Time: 22:42
 * To change this template use File | Settings | File Templates.
 */
public class QuartetNetworkList extends ArrayList<QuartetNetwork> {


    public QuartetNetworkList() {
        super();
    }

    public QuartetNetworkList(QuartetNetwork initialElement) {
        super();
        this.add(initialElement);
    }

    public Taxa combineTaxaSets() {

        Taxa result = new Taxa();

        for(QuartetNetwork data : this) {
            result.addAll(data.getTaxa());
        }

        return result;
    }


    public List<Taxa> getTaxaSets() {

        List<Taxa> result = new ArrayList<>();

        for(QuartetNetwork data : this) {
            result.add(data.getTaxa());
        }

        return result;
    }


    public void translateTaxaIndicies(Taxa superTaxaSet) {
        for(QuartetNetwork data : this) {
            data.setTaxaIndecies(superTaxaSet);
        }
    }

    public List<Double> getWeights() {

        List<Double> weights = new ArrayList<>();

        for(QuartetNetwork data : this) {
            weights.add(data.getWeight());
        }

        return weights;
    }
}
