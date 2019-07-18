
/*
 * QUEST SOFTWARE PROPRIETARY INFORMATION
 *
 * This software is confidential.  Quest Software Inc., or one of its
 * subsidiaries, has supplied this software to you under terms of a
 * license agreement, nondisclosure agreement or both.
 *
 * You may not copy, disclose, or use this software except in accordance with
 * those terms.
 *
 *
 * Copyright 2019 Quest Software Inc.
 * ALL RIGHTS RESERVED.
 *
 * QUEST SOFTWARE INC. MAKES NO REPRESENTATIONS OR
 * WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT.  QUEST SOFTWARE SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author pchudani
 *
 */
public class Filter {
	public static void main(String[] args) {
		final Display display = new Display();

		final Shell shell = new Shell(display);

		shell.setLayout(new FillLayout());

		final Grid table = new Grid(shell, SWT.NONE);
		final GridColumn col = new GridColumn(table, SWT.NONE);
		final HeaderRenderer headerRenderer = new HeaderRenderer();
		col.setHeaderRenderer(headerRenderer);
		col.setText("Column");
		col.setWidth(200);
		table.setHeaderVisible(true);

		final Composite control = new Composite(col.getParent(), SWT.NONE);
		GridLayoutFactory.fillDefaults()
							  .numColumns(1)
							  .margins(4, 4)
							  .applyTo(control);
		Image image = new Image(display, "database_table-filter.png");
		image = new Image(display, image.getImageData()
												  .scaledTo(15, 15));
		final Label lbl = new Label(control, SWT.NONE);
		lbl.setImage(image);
		GridDataFactory.fillDefaults()
							.grab(true, true)
							.align(SWT.END, SWT.CENTER)
							.applyTo(lbl);
		col.setHeaderControl(control);

		lbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				final MessageDialog dialog = new MessageDialog(shell, "Filter", null, "Let's filter", MessageDialog.INFORMATION, new String[] { "Let's do it", "Rather not" }, 0);
				dialog.open();
			}
		});

		table.addListener(SWT.MouseUp, event -> {
			if (headerRenderer.getFilterBounds()
									.contains(event.x, event.y)) {
				final MessageDialog dialog = new MessageDialog(shell, "Filter", null, "Let's filter", MessageDialog.INFORMATION, new String[] { "Let's do it", "Rather not" }, 0);
				dialog.open();
			}
		});

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
