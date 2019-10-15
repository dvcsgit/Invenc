/*
 * Copyright (c) 2014.
 * This Project and its content is copyright of ftc
 * All rights reserved.
 */

package fpg.ftc.si.pfg_inventory.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

//import static fpg.ftc.patrol.utils.LogUtils.*;
//import static fpg.ftc.patrol.utils.LogUtils.LOGD;
//import static fpg.ftc.patrol.utils.LogUtils.LOGE;
//import static fpg.ftc.patrol.utils.LogUtils.LOGI;
//import static fpg.ftc.patrol.utils.LogUtils.makeLogTag;


/**
 * 檔案 解壓縮 壓縮處理
 * Created by MarlinJoe on 2014/3/20.
 */
public class FileZipUtils {

    //private static final String TAG = makeLogTag(FileZipUtils.class);


    /**
     * 解壓縮
     * fix 抄表資料不進行覆蓋 也就是不會清除抄表資料
     * @param source 來源位置
     * @param destination 目標位置
     * @param ignoreFile 忽略壓縮檔裡面的檔案 如果目標位置有該檔案則忽略 若沒有則新增
     */
    public static String[] unzip(String source, String destination,String ignoreFile){

//        LOGI(TAG,"unzip src:"+source);
//        LOGI(TAG,"unzip dest:"+destination);
        final int BUFFER_SIZE = 4096;
        int i=0;
        String[] arryfileNameList;
        arryfileNameList=new String[10];

        BufferedOutputStream bufferedOutputStream = null;
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(source);
            ZipInputStream zipInputStream
                    = new ZipInputStream(new BufferedInputStream(fileInputStream));
            ZipEntry zipEntry;

            while ((zipEntry = zipInputStream.getNextEntry()) != null){

                String zipEntryName = zipEntry.getName();
                File file = new File(destination + zipEntryName);

                String fileName = file.getName();

                String[] arryfileName=fileName.split("\\.");
                arryfileNameList[i]=arryfileName[0];
                i++;
//                strinsert="insert into "+Constants.SQLLITE_DOWNLOAD_MENU_TABLE+
//                        "("+Constants.BOOK+") values ('"+aryfileName[0]+"')";
//                dbHelper.InsertMenu(strinsert);

                if(fileName.equals(ignoreFile))
                {
                    //去檢查檔案在不在
                    // 檔案存在的話會刪除掉
                    if (file.exists()){
                        //LOGI(TAG,"此檔案"+fileName+"目標已存在,忽略不新增");
                        continue;
                    }
                }
                else
                {
                    // 檔案存在的話會刪除掉(進行覆蓋的動作)
                    if (file.exists()){
                        file.delete();
                        //LOGI(TAG,"unzip"+file.getAbsolutePath()+" Delete");
                    }
                }



                if(zipEntry.isDirectory()){
                    file.mkdirs();
                }else{
                    byte buffer[] = new byte[BUFFER_SIZE];
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    bufferedOutputStream
                            = new BufferedOutputStream(fileOutputStream, BUFFER_SIZE);
                    int count;

                    while ((count
                            = zipInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        bufferedOutputStream.write(buffer, 0, count);
                    }

                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                }

            }
            zipInputStream.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  arryfileNameList;
    }


    /**
     * 壓縮
     * @param source 要壓縮的檔案 xxx
     * @param destination 壓縮後存放的位置 ex xxx/test.zip
     */
    public static void zip(String source,String destination)
    {
        //LOGI(TAG,"zip src:"+source);
        //LOGI(TAG,"zip dest:"+destination);
        try {

            // create byte buffer
            byte[] buffer = new byte[1024];

            FileOutputStream fos = new FileOutputStream(destination);

            ZipOutputStream zos = new ZipOutputStream(fos);

            File dir = new File(source);

            //檢查是檔案還是目錄
            if(dir.isDirectory())
            {
                File[] files = dir.listFiles();

                for (int i = 0; i < files.length; i++) {

                    //LOGD(TAG,"Adding file: " + files[i].getName());
                    FileInputStream fis = new FileInputStream(files[i]);

                    // begin writing a new ZIP entry, positions the stream to the start of the entry data
                    zos.putNextEntry(new ZipEntry(files[i].getName()));

                    int length;

                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }

                    zos.closeEntry();

                    // close the InputStream
                    fis.close();
                }
            }
            else
            {
                FileInputStream fis = new FileInputStream(dir);
                zos.putNextEntry(new ZipEntry(dir.getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                zos.closeEntry();

                // close the InputStream
                fis.close();
            }




            // close the ZipOutputStream
            zos.close();

        }
        catch (IOException ex) {
            //LOGE(TAG,ex.getMessage());
        }
    }



    public static void zip(List<String> source,String destination)
    {
//        LOGI(TAG,"zip src:"+source);
//        LOGI(TAG,"zip dest:"+destination);
        try {

            // create byte buffer
            byte[] buffer = new byte[1024];

            FileOutputStream fos = new FileOutputStream(destination);

            ZipOutputStream zos = new ZipOutputStream(fos);

            for(String fileName : source )
            {
                //LOGD(TAG,"Adding file: " + fileName);
                FileInputStream fis = new FileInputStream(fileName);
                // begin writing a new ZIP entry, positions the stream to the start of the entry data
                zos.putNextEntry(new ZipEntry(fileName));
                int length;

                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                zos.closeEntry();

                // close the InputStream
                fis.close();
            }


            // close the ZipOutputStream
            zos.close();

        }
        catch (IOException ex) {
            //+LOGE(TAG,ex.getMessage());
        }
    }
}
