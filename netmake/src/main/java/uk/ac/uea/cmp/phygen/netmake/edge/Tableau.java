package uk.ac.uea.cmp.phygen.netmake.edge;

import uk.ac.uea.cmp.phygen.core.ds.Split;
import uk.ac.uea.cmp.phygen.core.ds.SplitBlock;
import uk.ac.uea.cmp.phygen.core.ds.SplitSystem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Sarah Bastkowski
 * See Sarah Bastkowski, 2010:
 * <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 */
public class Tableau<E> {

    private ArrayList<ArrayList<E>> components;

    /**
     * Constructs an empty tableau.
     */
    public Tableau() {
        components = new ArrayList<ArrayList<E>>();
    }

    /**
     * Creates a new copy of a Tableau using the instance provided.
     * @param copy
     */
    public Tableau( Tableau<E> copy )
    {
        this();

        for(ArrayList<E> row : copy.components)
        {
            ArrayList<E> newRow = new ArrayList<E>();

            for(E e : row)
            {
//                not good enough!...
                newRow.add(e);
            }

            this.components.add(newRow);
        }

//        probably not sufficient
        //this.components.addAll(copy.components);
    }

    public SplitSystem convertToSplitSystem(int nbTaxa) {

        List<Split> splits = new ArrayList<>();

        int rows = this.rows();

        for(int i = 0; i < rows; i++) {

            int[] data = new int[rows];

            for(int j = 0; j < rows; j++) {

                E e = this.get(i, j);
                data[j] = Integer.parseInt(e.toString());
            }

            splits.add(new Split(new SplitBlock(data), nbTaxa));
        }

        return new SplitSystem(splits, null, null);
    }

    /**
     * Returns the number of elements in this tableau.
     *
     * @return number of elements in this tableau
     */
    public int size() {
        int size = 0;

        for (ArrayList<E> aList : components) {
            size += aList.size();
        }

        return size;
    }

    /**
     * Returns the element at the specified position in this tableau.
     *
     * @param row index of the row of the element to return
     * @param col column of the element to return
     * @return the element at the specified position in this tableau
     * @throws IndexOutOfBoundsException if the position is out of range
     */
    public E get(int row, int col) {
        return components.get(row).get(col);
    }

    /**
     * Returns the row at the specified position in this tableau.
     *
     * @param row index of the row to be returned
     * @return the row at the specified position in this tableau
     * @throws IndexOutOfBoundsException if the row is out of range
     */
    public ArrayList<E> getRow(int row) {
        return components.get(row);
    }

    /**
     * Returns the number of rows in this tableau.
     *
     * @return the number of rows in this tableau
     */
    public int rows() {
        return components.size();
    }

    /**
     * Returns the number of elements of the specified row in this
     * tableau.
     *
     * @param row number of the row
     * @return the number of elements of the specified row in this tableau
     * @throws IndexOutOfBoundsException if the row is out of range
     */
    public int rowSize(int row) {
        return components.get(row).size();
    }

    /**
     * Appends the specified row to the bottom of the tableau.
     *
     * @param newRow row to be appended to the tableau
     * @return true
     */
    public boolean addRow(ArrayList<E> newRow) {
        return components.add(newRow);
    }

    /**
     * Appends the specified row to the bottom of the tableau.
     *
     * @param newRow row to be appended to the tableau
     * @return true
     */
    public boolean addRow(E[] newRow) {
        ArrayList<E> newList = new ArrayList<E>();

        for (E elem : newRow) {
            newList.add(elem);
        }

        return components.add(newList);
    }

    /**
     * Appends the specified element to a new row
     * at the bottom of the tableau.
     *
     * @param newRow element to be appended to a new row
     * @return true
     */
    public boolean addRow(E newRow) {
        ArrayList<E> newList = new ArrayList<E>();

        newList.add(newRow);

        return components.add(newList);
    }


    public void addAllRows(Tableau t)
    {
        for(int i = 0; i < t.rows(); i++)
        {
            components.add( t.copyRow(i) );
        }
    }

    /**
     * Appends the element to the specified row in this tableau. If the
     * specified row is not existent it will be created. Any necessary new
     * preceding rows will be added as empty rows.
     *
     * @param row index of the row
     * @param element
     * @throws IndexOutOfBoundsException if the row index is out of range
     */
    public void appendToRow(int row, E element) {
        while (components.size() < (row + 1)) {
            components.add(new ArrayList<E>());
        }

        components.get(row).add(element);
    }

    /**
     * Creates a new copy of a row in this Tableau
     * @param row The index of the row to duplicate
     * @return The duplicated row
     */
    public ArrayList<E> copyRow(int row) {
        ArrayList<E> oldrow = components.get( row );
        ArrayList<E> newrow = new ArrayList<E>();
        newrow.addAll((ArrayList<E>)oldrow.clone());
        return newrow;
    }

    /**
     * Replaces the element at the specified position in this tableau
     * with the specified element.
     *
     * @param row row of the element to replace
     * @param col column of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the position is out of range
     */
    public E set(int row, int col, E element) {
        return components.get(row).set(col, element);
    }

    /**
     * Removes the row at the specified position in the tableau.
     * Shifts any subsequent rows up.
     *
     * @param row number of the row to be removed
     * @return the row that was removed from the tableau
     * @throws IndexOutOfBoundsException if the row is out of range
     */
    public ArrayList<E> removeRow(int row) {
        return components.remove(row);
    }

    /**
     * Removes the element at the specified position in this tableau.
     * Shifts any subsequent elements to the left (subtracts one from
     * their indices).
     *
     * @param row row of the element to be removed
     * @param col column of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the position is out of range
     */
    public E remove(int row, int col) {
        return components.get(row).remove(col);
    }

    /**
     * Reverses the order of the elements of the row
     * at the specified position in this tableau.
     *
     * @param row number of the row to be reversed
     */
    public void reverseRow(int row) {
        ArrayList<E> oldRow = components.get(row);
        ArrayList<E> newRow = new ArrayList<E>();

        for (int i = oldRow.size() - 1; i >= 0; i--) {
            newRow.add(oldRow.get(i));
        }

        components.set(row, newRow);
    }

    /**
     * Appends a row at a specified position in this tableau
     * to another row.
     *
     * @param row1 number of the row that row2 is appended to
     * @param row2 number of the row to be appended to row1
     * @throws IndexOutOfBoundsException if a row is out of range
     */
    public void mergeRows(int row1, int row2) {
        ArrayList<E> list1 = components.get(row1);
        ArrayList<E> list2 = components.get(row2);

        list1.addAll(list2);

        components.remove(row2);
    }

}
