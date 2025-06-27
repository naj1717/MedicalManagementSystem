/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common;

import java.io.File;
import javax.swing.JOptionPane;
import mns.MedicalUtils;

/**
 *
 * @author mulan
 */
public class OpenPdf {
    public static void openById(String id)
    {
        try
        {
            if((new File(MedicalUtils.billPath+id+".pdf")).exists())
            {
                Process p = Runtime
                        .getRuntime()
                        .exec("rundll32 url.dll,FileProtocolHandler "+MedicalUtils.billPath+""+id+".pdf");
            }
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, e);

        }
    }
}
