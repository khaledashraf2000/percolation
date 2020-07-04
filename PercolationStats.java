public class PercolationStats {
    private  int trials;
    private double[] x;
    private double mean;
    private double stddev;
    private double confidenceLo, confidenceHi;

    public PercolationStats(int n, int trials) {
        if (n > 0 && trials > 0) {
            this.trials = trials;
            // fraction of open sites
            this.x = new double[trials];
            // repeat trials times
            while (trials-- > 0) {
                Percolation p = new Percolation(n);
                while (!p.percolates()) {
                    // open random site (row, col)
                    int row = (int) (Math.random() * n) + 1;
                    int col = (int) (Math.random() * n) + 1;
                    p.open(row, col);
                }
                // add this trial fraction
                this.x[x.length - (trials + 1)] = (double) p.numberOfOpenSites() / (n * n);
            }
            // calculate statistics
            this.mean = mean();
            this.stddev = stddev();
            this.confidenceLo = confidenceLo();
            this.confidenceHi = confidenceHi();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            // square side length of percolation
            int n = Integer.parseInt(args[0]);
            // number of trials
            int T = Integer.parseInt(args[1]);
            PercolationStats pstats = new PercolationStats(n, T);
            pstats.printStats();
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        double sum = 0;
        for (double x : this.x) {
            sum += x;
        }
        return sum / this.trials;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        double sum = 0;
        for (double x : this.x) {
            sum += Math.pow((x - mean), 2);
        }
        return Math.pow(sum / (this.trials - 1), 0.5);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean - (1.96 * stddev) / Math.pow(trials, 0.5);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean + (1.96 * stddev) / Math.pow(trials, 0.5);
    }

    public void printStats() {
        System.out.println("mean = \t\t\t\t" + this.mean);
        System.out.println("stddev = \t\t\t" + this.stddev);
        System.out.println("95% confidence interval = \t[" + this.confidenceLo +
                                   ", " + this.confidenceHi + "]");
    }
}
