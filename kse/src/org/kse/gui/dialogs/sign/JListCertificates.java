package org.kse.gui.dialogs.sign;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.kse.gui.JKseTable;
import org.kse.gui.KseFrame;
import org.kse.gui.PlatformUtil;
import org.kse.utilities.history.KeyStoreHistory;

public class JListCertificates extends JPanel {

	private static final long serialVersionUID = 1L;
	private static ResourceBundle res = ResourceBundle.getBundle("org/kse/gui/dialogs/sign/resources");

	private JScrollPane jspListCertsTable;
	private JKseTable jtListCerts;

	private JFrame parent;
	private KseFrame kseFrame;
	
	List<X509Certificate> listCertificados = new ArrayList<>();

	public JListCertificates(JFrame parent, KseFrame kseFrame) {
		super();
		this.parent = parent;
		this.kseFrame = kseFrame;
		initComponents();
	}

	private void initComponents() {

		ListCertsTableModel rcModel = new ListCertsTableModel();
		jtListCerts = new JKseTable(rcModel);
		RowSorter<ListCertsTableModel> sorter = new TableRowSorter<>(rcModel);
		jtListCerts.setRowSorter(sorter);

		jtListCerts.setShowGrid(false);
		jtListCerts.setRowMargin(0);
		jtListCerts.getColumnModel().setColumnMargin(0);
		jtListCerts.getTableHeader().setReorderingAllowed(false);
		jtListCerts.setAutoResizeMode(JKseTable.AUTO_RESIZE_ALL_COLUMNS);
		jtListCerts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		for (int i = 0; i < jtListCerts.getColumnCount(); i++) {
			TableColumn column = jtListCerts.getColumnModel().getColumn(i);

			if (i == 0) {
				column.setPreferredWidth(100);
			}

			column.setHeaderRenderer(new ListCertsTableHeadRend(jtListCerts.getTableHeader().getDefaultRenderer()));
			column.setCellRenderer(new ListCertsTableCellRend());
		}

		jspListCertsTable = PlatformUtil.createScrollPane(jtListCerts, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jspListCertsTable.getViewport().setBackground(jtListCerts.getBackground());

		this.setLayout(new BorderLayout(5, 5));
		this.setPreferredSize(new Dimension(100, 200));
		this.add(jspListCertsTable, BorderLayout.CENTER);
		this.setBorder(new CompoundBorder(new EtchedBorder(), new EmptyBorder(5, 5, 5, 5)));
	}

	public void load(KeyStoreHistory keyStoreHistory) throws KeyStoreException {
		
		listCertificados.clear();
		KeyStore tempTrustStore = keyStoreHistory.getCurrentState().getKeyStore();
		Enumeration<String> enumeration = tempTrustStore.aliases();
		while (enumeration.hasMoreElements()) {
			String alias = enumeration.nextElement();
			X509Certificate cert = (X509Certificate) tempTrustStore.getCertificate(alias);
			listCertificados.add(cert);
		}
		ListCertsTableModel rcModel = (ListCertsTableModel) jtListCerts.getModel();
		rcModel.load(listCertificados);
	}
	
	public X509Certificate getCertSelected() {
		int pos = jtListCerts.getSelectedRow();
		if (pos >= 0) {
			return listCertificados.get(pos);
		}
		else {
			return null;
		}
	}
}
