package app.contestTimetable.pdf;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;

//public class DottedLineCellRenderer extends CellRenderer {
//
//    boolean[] borders;
//
//    public DottedLineCellRenderer(Cell modelElement, boolean[] borders) {
//        super(modelElement);
//        this.borders = new boolean[borders.length];
//
//        for (int i = 0; i < this.borders.length; i++) {
//            this.borders[i] = borders[i];
//        }
//    }
//
//    // If renderer overflows on the next area, iText uses getNextRender() method to create a renderer for the overflow part.
//    // If getNextRenderer isn't overriden, the default method will be used and thus a default rather than custom
//    // renderer will be created
//    @Override
//    public IRenderer getNextRenderer() {
//        return new DottedLineCellRenderer((Cell) modelElement, borders);
//    }
//
//    @Override
//    public void draw(DrawContext drawContext) {
//        super.draw(drawContext);
//        PdfCanvas canvas = drawContext.getCanvas();
//        Rectangle position = getOccupiedAreaBBox();
//        canvas.saveState();
//        canvas.setLineDash(1f,3f);
//
//        if (borders[0]) {
//            canvas.moveTo(position.getRight(), position.getTop());
//            canvas.lineTo(position.getLeft(), position.getTop());
//        }
//
//        if (borders[2]) {
//            canvas.moveTo(position.getRight(), position.getBottom());
//            canvas.lineTo(position.getLeft(), position.getBottom());
//        }
//
//        if (borders[3]) {
//            canvas.moveTo(position.getRight(), position.getTop());
//            canvas.lineTo(position.getRight(), position.getBottom());
//        }
//
//        if (borders[1]) {
//            canvas.moveTo(position.getLeft(), position.getTop());
//            canvas.lineTo(position.getLeft(), position.getBottom());
//        }
//
//        canvas.stroke();
//        canvas.restoreState();
//    }
//}
