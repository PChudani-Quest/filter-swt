import org.eclipse.nebula.widgets.grid.AbstractRenderer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

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
public class FilterRenderer extends AbstractRenderer {

	private Image image;

	/**
	 * Default constructor.
	 */
	public FilterRenderer() {
		super();

		setSize(15, 15);
		image = new Image(getDisplay(), "database_table-filter.png");
		image = new Image(getDisplay(), image.getImageData()
														 .scaledTo(15, 15));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(GC gc, Object value) {
		gc.drawImage(image, getBounds().x, getBounds().y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point computeSize(GC gc, int wHint, int hHint, Object value) {
		return new Point(15, 15);
	}
}
