package com.test.HowlFirebaseAuth.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;

import com.test.HowlFirebaseAuth.ValueObject.WorkInfo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by admin on 2017/10/13.
 */

public class CustomDocumentPrintAdapter extends PrintDocumentAdapter {

    private Paint mPaint = new Paint();
    private Context mContext;
    private List<WorkInfo> mWorkInfoList;

    PrintedPdfDocument mPdfDocument;

    public CustomDocumentPrintAdapter(Context context, List<WorkInfo> workInfoList) {
        mContext = context;
        mWorkInfoList = workInfoList;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes,
                         PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback, Bundle extras) {

        mPdfDocument = new PrintedPdfDocument(mContext, newAttributes);

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }
        int pages = 1;
        //newAttributes.getColorMode();
        //newAttributes.getMediaSize().getHeightMils();  //単位は1/1000インチ
        //newAttributes.getMediaSize().getWidthMils();   //単位は1/1000インチ

        PrintDocumentInfo info = new PrintDocumentInfo.Builder("androids.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(pages).build();
        callback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(PageRange[] pages,
                        ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal, WriteResultCallback callback) {
        if (mPdfDocument == null) {
            return;
        }

        PdfDocument.Page page = mPdfDocument.startPage(0);
        if (cancellationSignal.isCanceled()) {
            callback.onWriteCancelled();
            mPdfDocument.close();
            mPdfDocument = null;
            return;
        }
        onDraw(page.getCanvas());
        mPdfDocument.finishPage(page);

        try {
            mPdfDocument.writeTo(new FileOutputStream(destination
                    .getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            mPdfDocument.close();
            mPdfDocument = null;
        }
        callback.onWriteFinished(pages);
    };

    public void onDraw(Canvas canvas) {
        //レンダリング処理
        //72で1インチとなる
        //省略

        int titleBaseLine = 80;
        int contentBaseLine = 100;
        int leftMargin = 50;

        mPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("NUM", leftMargin + 0 , titleBaseLine, mPaint);
        canvas.drawText("名前", leftMargin + 80, titleBaseLine, mPaint);
        canvas.drawText("出勤時間", leftMargin + 220, titleBaseLine, mPaint);
        canvas.drawText("退勤時間", leftMargin + 360, titleBaseLine, mPaint);
        canvas.drawText("勤務時間", leftMargin + 460, titleBaseLine, mPaint);

        int marginHeight = 20;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd(E) HH:mm", new Locale("en", "US"));
        try{
            for(int i=0; i<mWorkInfoList.size(); i++){
                canvas.drawText(""+(i+1), leftMargin + 0 , contentBaseLine + ( (i+1) * marginHeight ), mPaint);
                canvas.drawText(mWorkInfoList.get(i).getName(), leftMargin + 80, contentBaseLine + ( (i+1) * marginHeight ), mPaint);
                canvas.drawText(dateFormat.format(mWorkInfoList.get(i).getCreateOnWorkDate()), leftMargin + 220, contentBaseLine + ( (i+1) * marginHeight ), mPaint);
                canvas.drawText(dateFormat.format(mWorkInfoList.get(i).getCreateOffWorkDate()), leftMargin + 360, contentBaseLine + ( (i+1) * marginHeight ), mPaint);
                canvas.drawText(String.format("%2d", mWorkInfoList.get(i).getWorkingTime() / 60) + ":" +
                        String.format("%2d", mWorkInfoList.get(i).getWorkingTime() % 60) ,
                        leftMargin + 460, contentBaseLine + ( (i+1) * marginHeight ), mPaint);
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }

    }
}
