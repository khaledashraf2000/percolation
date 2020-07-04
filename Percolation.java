/*******************************************************************************
 * We model a percolation system using an n-by-n grid of sites. Each site is
 * either open or blocked. A full site is an open site that can be connected to
 * an open site in the top row via a chain of neighboring (left, right, up, down)
 * open sites. We say the system percolates if there is a full site in the
 * bottom row. In other words, a system percolates if we fill all open sites
 * connected to the top row and that process fills some open site on the bottom
 * row.
 ******************************************************************************/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Percolation {
    private int[] id;
    private int[] sz;
    private boolean[] open;
    private int openCounter;
    private int n;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n is less than or equals to zero.\n");
        }
        this.n = n;
        // 2 places are added for virtual points
        id = new int[n * n + 2];
        sz = new int[n * n + 2];
        // all sites are initially blocked
        open = new boolean[n * n + 2];
        openCounter = 0;
        for (int i = 0; i < n * n + 2; i++) {
            id[i] = i;
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader("/tests/" + args[0]));
                int n = Integer.parseInt(reader.readLine());
                Percolation p = new Percolation(n);
                while (reader.ready()) {
                    String[] line = reader.readLine().split(" ");
                    int[] pos = new int[] { Integer.parseInt(line[0]), Integer.parseInt(line[1]) };
                    p.open(pos[0], pos[1]);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (isValidBounds(row, col)) {
            int currpos = pos(row, col);

            // first set current position as open
            open[currpos] = true;
            openCounter++;

            int up = pos(row - 1, col);
            int down = pos(row + 1, col);
            int left = pos(row, col - 1);
            int right = pos(row, col + 1);

            // if any of the surrounding sites are also open then union
            // is right out of grid
            if (col < n && open[right]) {
                union(currpos, right);
            }
            // is left out of grid
            if (col > 1 && open[left]) {
                union(currpos, left);
            }
            // is up out of grid
            if (row > 1 && open[up]) {
                union(currpos, up);
            }
            // is down out of grid
            if (row < n && open[down]) {
                union(currpos, down);
            }
        }
        else {
            throw new IllegalArgumentException("Out of bounds.\n");
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (isValidBounds(row, col)) {
            return open[pos(row, col)];
        }
        else {
            throw new IllegalArgumentException("Out of bounds.\n");
        }
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (isValidBounds(row, col)) {
            if (isOpen(row, col)) {
                if (row > 1) {
                    for (int i = 1; i <= n; i++) {
                        if (connected(pos(row, col), pos(1, i))) {
                            return true;
                        }
                    }
                }
                else {
                    return isOpen(row, col);
                }
            }
            return false;
        }
        else {
            throw new IllegalArgumentException("Out of bounds.\n");
        }
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openCounter;
    }

    // does the system percolate?
    public boolean percolates() {
        // connect open upper points with upper virtual point
        // at n * n + 2 - 1 - 1
        // and open lower points with lower virtual point
        // at n * n + 2 - 1
        int uvp = n * n + 2 - 1 - 1;
        open[uvp] = true;
        int lvp = n * n + 2 - 1 - 0;
        open[lvp] = true;
        for (int i = 1; i <= n; i++) {
            if (isOpen(1, i)) {
                union(pos(1, i), uvp);
            }
            if (isOpen(n, i)) {
                union(pos(n, i), lvp);
            }
        }
        return connected(uvp, lvp);
    }

    private void union(int p, int q) {
        if (!connected(p, q) && open[p] && open[q]) {
            // find lighter tree and add it to heavier
            if (sz[p] > sz[q]) {
                id[root(q)] = root(p);
                sz[p] += sz[q];
            }
            else {
                id[root(p)] = root(q);
                sz[q] += sz[p];
            }
        }
    }

    private boolean connected(int p, int q) {
        return root(p) == root(q);
    }

    private int root(int i) {
        if (i == id[i]) {
            return i;
        }
        id[i] = id[id[i]];
        return root(id[i]);
    }

    /*****************************************/
    /************ Utility Methods ************/

    private boolean isValidBounds(int row, int col) {
        return row >= 1 && col >= 1 && row <= n && col <= n;
    }

    private int pos(int row, int col) {
        return n * (row - 1) + (col - 1);
    }
}
