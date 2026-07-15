package com.zzyl.common.test;

import com.zzyl.common.utils.PDFUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PDFUtilTest {

    public static void main(String[] args) throws FileNotFoundException {
        //读一个文件
        FileInputStream fis =  new FileInputStream("F:\\temp\\体检报告-刘爱国-男-69岁.pdf");
        //读取文件为字符串
        String result = PDFUtil.pdfToString(fis);
        System.out.println(result);
    }
}
