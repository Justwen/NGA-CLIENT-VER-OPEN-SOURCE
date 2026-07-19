package gov.anzong.androidnga.common.util;

import android.content.res.AssetManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import gov.anzong.androidnga.base.util.ContextUtils;

/**
 * @author yangyihang
 */
public class FileUtils {

    public static String readAssetToString(String path) {
        AssetManager assetManager = ContextUtils.getContext().getAssets();
        try (InputStream is = assetManager.open(path)) {
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean zipFiles(String scrPath, String destPath) {
        File destFile = new File(destPath);
        destFile.getParentFile().mkdirs();
        try (ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(destFile.getAbsolutePath()))) {
            //压缩
            zipFiles(scrPath, null, outZip);
            return true;
        } catch (IOException e) {
            LogUtils.print(e);
        }
        return false;
    }

    private static void zipFiles(String srcPath, String destZipPath, ZipOutputStream zos) throws IOException {
        if (zos == null) {
            return;
        }
        if (destZipPath == null) {
            destZipPath = "";
        }
        File scrFile = new File(srcPath);
        String fileName = scrFile.getName();
        if (scrFile.isFile()) {
            try (FileInputStream is = new FileInputStream(scrFile)) {
                ZipEntry zipEntry = new ZipEntry(destZipPath + fileName);
                zos.putNextEntry(zipEntry);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
            }
        } else {
            //文件夹
            String[] fileList = scrFile.list();
            if (fileList == null) {
                return;
            }
            String currentZipDir = destZipPath + fileName + File.separator;
            ZipEntry zipEntry = new ZipEntry(currentZipDir);
            zos.putNextEntry(zipEntry);
            zos.closeEntry();
            //子文件和递归
            for (String child : fileList) {
                zipFiles(srcPath + File.separator + child, currentZipDir, zos);
            }

        }
    }

    public static void unzip(String scrZip, String destPath) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(scrZip)))) {
            ZipFile zipFile = new ZipFile(scrZip);
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                String outputPath = destPath + File.separator + zipEntry.getName();
                File outputFile = new File(outputPath);
                if (zipEntry.isDirectory()) {
                    outputFile.mkdirs();
                } else {
                    byte[] buffer = new byte[1024];
                    try (OutputStream os = new FileOutputStream(outputFile);
                         // 通过ZipFile的getInputStream方法拿到具体的ZipEntry的输入流
                         InputStream is = zipFile.getInputStream(zipEntry)) {
                        int len = 0;
                        while ((len = is.read(buffer)) != -1) {
                            os.write(buffer, 0, len);
                        }
                    }
                }

            }
        } catch (IOException e) {
            LogUtils.print(e);
        }
    }
}
