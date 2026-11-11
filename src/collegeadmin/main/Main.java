
package collegeadmin.main;

import javax.swing.SwingUtilities;
import collegeadmin.MainDashboard;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new MainDashboard();
        });

    }
}
