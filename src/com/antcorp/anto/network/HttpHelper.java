package com.antcorp.anto.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.widget.UtilStatics;

public class HttpHelper {

    static Document mDoc = null;
    static String   mscsrf_token = new String();
    static String   mscsrf_param = new String();
    
    public static String request(HttpResponse response)
{
    String result = "";
    try{
        InputStream in = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder str = new StringBuilder();
        String line = null;
        
        while((line = reader.readLine()) != null){
            str.append(line + "\n");
        }
       
        in.close();
        
        result = str.toString();
        
        testVersionToken(result);


    }catch(Exception ex){
        result = "Error";
    }
    return result;
}

private static void testVersionToken(String result) {
    UtilStatics.AntCorpInfo(result);
    mscsrf_param = "authenticity_token";
    mscsrf_token =GlobalData.m_data.msToken;
		
	}

public static Document GetDocument(String psStr)
{
    return mDoc;
}
    
public static boolean CreateDocument(String psStr)
{
            try {
                    mDoc = null;
                    
                    if (!psStr.contains("<WIRESData>"))
                            return false;
                    
                    DocumentBuilderFactory  dbf2 =  DocumentBuilderFactory.newInstance();
                    DocumentBuilder db2;

                    db2 = dbf2.newDocumentBuilder();
                    
                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader(psStr));
            
                    mDoc = db2.parse(is);
                    
                    return true;
                    
            } catch (SAXException e) {
                    mDoc = null;

            } catch (IOException e) {
                    mDoc = null;

            } catch (ParserConfigurationException e) {
                    mDoc = null;

            }
            
            return false;
}

protected static void GetForgeryProtect()
{
    mscsrf_param = mscsrf_token = "";
    if (mDoc == null)
            return;
    
    String lsName;
    
    NodeList nodes = mDoc.getElementsByTagName("meta");
    
    // iterate the employees
    for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);

            if (element.hasAttribute("name")){
                    lsName = element.getAttribute("name");
                    
                    if (lsName.compareToIgnoreCase("csrf-token") == 0)
                            mscsrf_token = element.getAttribute("content");
                    
                    if (lsName.compareToIgnoreCase("csrf-param") == 0)
                            mscsrf_param = element.getAttribute("content");
            }
    }
}

public static boolean HasProtectForgery()
{
    if (mscsrf_token.trim().compareTo("") != 0 &&
            mscsrf_param.trim().compareTo("") != 0)
            return true;

    return false;

}

public static String GetForgeryToken()
{
    return mscsrf_token;
}

public static String GetForgeryParam()
{
    return mscsrf_param;
}
}
