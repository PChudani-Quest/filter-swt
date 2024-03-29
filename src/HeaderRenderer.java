import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridHeaderRenderer;
import org.eclipse.nebula.widgets.grid.internal.SortArrowRenderer;
import org.eclipse.nebula.widgets.grid.internal.TextUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Display;

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

/**
 * @author pchudani
 *
 */
public class HeaderRenderer extends GridHeaderRenderer {

	int leftMargin = 6;

	int rightMargin = 6;

	int topMargin = 3;

	int bottomMargin = 3;

	int arrowMargin = 6;

	int imageSpacing = 3;

	private final SortArrowRenderer arrowRenderer = new SortArrowRenderer();

	private TextLayout textLayout;

	private final FilterRenderer filterRenderer;

	/**
	 * Constructor
	 *
	 */
	public HeaderRenderer() {
		filterRenderer = new FilterRenderer();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point computeSize(GC gc, int wHint, int hHint, Object value) {
		final GridColumn column = (GridColumn) value;

		gc.setFont(column.getHeaderFont());

		int x = leftMargin;
		int y = topMargin + gc.getFontMetrics()
									 .getHeight()
				+ bottomMargin;

		if (column.getImage() != null) {
			x += column.getImage()
						  .getBounds().width
					+ imageSpacing;

			y = Math.max(y,
							 topMargin + column.getImage()
													 .getBounds().height
									 + bottomMargin);
		}
		if (!isWordWrap()) {
			x += gc.stringExtent(column.getText()).x + rightMargin;
		} else {
			int plainTextWidth;
			if (wHint == SWT.DEFAULT) {
				plainTextWidth = getBounds().width - x - rightMargin;
			} else {
				plainTextWidth = wHint - x - rightMargin;
			}

			getTextLayout(gc, column);
			textLayout.setText(column.getText());
			textLayout.setWidth(plainTextWidth < 1 ? 1
																: plainTextWidth);

			x += plainTextWidth + rightMargin;

			int textHeight = topMargin;
			textHeight += textLayout.getBounds().height;
			textHeight += bottomMargin;

			y = Math.max(y, textHeight);
		}

		y += computeControlSize(column).y;

		return new Point(x, y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(GC gc, Object value) {
		final GridColumn column = (GridColumn) value;

		// set the font to be used to display the text.
		gc.setFont(column.getHeaderFont());

		final boolean flat = (column.getParent()
											 .getCellSelectionEnabled()
				&& !column.getMoveable());

		final boolean drawSelected = ((isMouseDown() && isHover()));

		gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

		if (flat && isSelected()) {
			gc.setBackground(column.getParent()
										  .getCellHeaderSelectionBackground());
		}

		gc.fillRectangle(getBounds().x, getBounds().y, getBounds().width, getBounds().height);

		int pushedDrawingOffset = 0;
		if (drawSelected) {
			pushedDrawingOffset = 1;
		}

		int x = leftMargin;

		if (column.getImage() != null) {
			int y = bottomMargin;

			if (column.getHeaderControl() == null) {
				y = (getBounds().y + pushedDrawingOffset + getBounds().height) - bottomMargin - column.getImage()
																																  .getBounds().height;
			}

			gc.drawImage(column.getImage(), getBounds().x + x + pushedDrawingOffset, y);
			x += column.getImage()
						  .getBounds().width
					+ imageSpacing;
		}

		int width = getBounds().width - x;

		if (column.getSort() == SWT.NONE) {
			width -= rightMargin;
		} else {
			width -= arrowMargin + arrowRenderer.getSize().x + arrowMargin;
		}

		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));

		int y = bottomMargin;

		if (column.getHeaderControl() == null) {
			y = (getBounds().y + getBounds().height) - bottomMargin - gc.getFontMetrics()
																							.getHeight();
		} else {
			y = (getBounds().y + getBounds().height) - bottomMargin - gc.getFontMetrics()
																							.getHeight()
					- computeControlSize(column).y;
		}

		String text = column.getText();

		if (!isWordWrap()) {
			text = TextUtils.getShortString(gc, text, width);
			// y -= gc.getFontMetrics().getHeight();
		}

		if (column.getAlignment() == SWT.RIGHT) {
			final int len = gc.stringExtent(text).x;
			if (len < width) {
				x += width - len;
			}
		} else if (column.getAlignment() == SWT.CENTER) {
			final int len = gc.stringExtent(text).x;
			if (len < width) {
				x += (width - len) / 2;
			}
		}

		if (!isWordWrap()) {
			gc.drawString(text, getBounds().x + x + pushedDrawingOffset, y + pushedDrawingOffset, true);
		} else {
			getTextLayout(gc, column);
			textLayout.setWidth(width < 1 ? 1
													: width);
			textLayout.setText(text);
			y -= textLayout.getBounds().height;

			// remove the first line shift
			y += gc.getFontMetrics()
					 .getHeight();

			if (column.getParent()
						 .isAutoHeight()) {
				column.getParent()
						.recalculateHeader();
			}

			textLayout.draw(gc, getBounds().x + x + pushedDrawingOffset, y + pushedDrawingOffset);
		}

		if (column.getSort() != SWT.NONE) {
			if (column.getHeaderControl() == null) {
				y = getBounds().y + ((getBounds().height - arrowRenderer.getBounds().height) / 2) + 1;
			} else {
				y = getBounds().y + ((getBounds().height - computeControlSize(column).y - arrowRenderer.getBounds().height) / 2) + 1;
			}

			arrowRenderer.setSelected(column.getSort() == SWT.UP);
			if (drawSelected) {
				arrowRenderer.setLocation(((getBounds().x + getBounds().width) - arrowMargin - arrowRenderer.getBounds().width) + 1, y);
			} else {
				if (column.getHeaderControl() == null) {
					y = getBounds().y + ((getBounds().height - arrowRenderer.getBounds().height) / 2);
				} else {
					y = getBounds().y + ((getBounds().height - computeControlSize(column).y - arrowRenderer.getBounds().height) / 2);
				}
				arrowRenderer.setLocation((getBounds().x + getBounds().width) - arrowMargin - arrowRenderer.getBounds().width, y);
			}
			arrowRenderer.paint(gc, null);
		}

		if (!flat) {

			if (drawSelected) {
				gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
			} else {
				gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
			}

			gc.drawLine(getBounds().x, getBounds().y, (getBounds().x + getBounds().width) - 1, getBounds().y);
			gc.drawLine(getBounds().x, getBounds().y, getBounds().x, (getBounds().y + getBounds().height) - 1);

			if (!drawSelected) {
				gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
				gc.drawLine(getBounds().x + 1, getBounds().y + 1, (getBounds().x + getBounds().width) - 2, getBounds().y + 1);
				gc.drawLine(getBounds().x + 1, getBounds().y + 1, getBounds().x + 1, (getBounds().y + getBounds().height) - 2);
			}

			if (drawSelected) {
				gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
			} else {
				gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
			}
			gc.drawLine((getBounds().x + getBounds().width) - 1, getBounds().y, (getBounds().x + getBounds().width) - 1, (getBounds().y + getBounds().height) - 1);
			gc.drawLine(getBounds().x, (getBounds().y + getBounds().height) - 1, (getBounds().x + getBounds().width) - 1, (getBounds().y + getBounds().height) - 1);

			if (!drawSelected) {
				gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
				gc.drawLine((getBounds().x + getBounds().width) - 2, getBounds().y + 1, (getBounds().x + getBounds().width) - 2, (getBounds().y + getBounds().height) - 2);
				gc.drawLine(getBounds().x + 1, (getBounds().y + getBounds().height) - 2, (getBounds().x + getBounds().width) - 2, (getBounds().y + getBounds().height) - 2);
			}

		} else {
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));

			gc.drawLine((getBounds().x + getBounds().width) - 1, getBounds().y, (getBounds().x + getBounds().width) - 1, (getBounds().y + getBounds().height) - 1);
			gc.drawLine(getBounds().x, (getBounds().y + getBounds().height) - 1, (getBounds().x + getBounds().width) - 1, (getBounds().y + getBounds().height) - 1);
		}

		if (drawSelected) {
			filterRenderer.setLocation(((getBounds().x + getBounds().width) - arrowMargin - filterRenderer.getBounds().width) + 1, y);
		} else {
			if (column.getHeaderControl() == null) {
				y = getBounds().y + ((getBounds().height - filterRenderer.getBounds().height) / 2);
			} else {
				y = getBounds().y + ((getBounds().height - computeControlSize(column).y - filterRenderer.getBounds().height) / 2);
			}
			filterRenderer.setLocation((getBounds().x + getBounds().width) - arrowMargin - filterRenderer.getBounds().width, y);
		}
		filterRenderer.paint(gc, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDisplay(Display display) {
		super.setDisplay(display);
		arrowRenderer.setDisplay(display);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean notify(int event, Point point, Object value) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Rectangle getTextBounds(Object value, boolean preferred) {
		final GridColumn column = (GridColumn) value;

		int x = leftMargin;

		if (column.getImage() != null) {
			x += column.getImage()
						  .getBounds().width
					+ imageSpacing;
		}

		final GC gc = new GC(column.getParent());
		gc.setFont(column.getParent()
							  .getFont());
		final int y = getBounds().height - bottomMargin - gc.getFontMetrics()
																			 .getHeight();

		final Rectangle bounds = new Rectangle(x, y, 0, 0);

		final Point p = gc.stringExtent(column.getText());

		bounds.height = p.y;

		if (preferred) {
			bounds.width = p.x;
		} else {
			int width = getBounds().width - x;
			if (column.getSort() == SWT.NONE) {
				width -= rightMargin;
			} else {
				width -= arrowMargin + arrowRenderer.getSize().x + arrowMargin;
			}
			bounds.width = width;
		}

		gc.dispose();

		return bounds;
	}

	public Rectangle getFilterBounds() {
		return filterRenderer.getBounds();
	}

	/**
	 * @return the bounds reserved for the control
	 */
	@Override
	protected Rectangle getControlBounds(Object value, boolean preferred) {
		final Rectangle bounds = getBounds();
		final GridColumn column = (GridColumn) value;
		final Point controlSize = computeControlSize(column);

		final int y = (getBounds().y + getBounds().height) - bottomMargin - controlSize.y;

		return new Rectangle(bounds.x + 3, y, bounds.width - 6, controlSize.y);
	}

	private Point computeControlSize(GridColumn column) {
		if (column.getHeaderControl() != null) {
			return column.getHeaderControl()
							 .computeSize(SWT.DEFAULT, SWT.DEFAULT);
		}
		return new Point(0, 0);
	}

	private void getTextLayout(GC gc, GridColumn column) {
		if (textLayout == null) {
			textLayout = new TextLayout(gc.getDevice());
			textLayout.setFont(gc.getFont());
			column.getParent()
					.addDisposeListener(e -> textLayout.dispose());
		}
		textLayout.setAlignment(column.getAlignment());
	}
}
