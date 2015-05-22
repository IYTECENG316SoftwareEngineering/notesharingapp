package com.onur.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.simple.JSONObject;

import com.onur.database.DBConnectionHandler;

/**
 * Servlet implementation class FileUploadServlet
 */
@MultipartConfig
public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		upload(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		upload(request,response);
		
	}
	
	private void upload(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {

		  Part p = request.getPart("userfile");
		  String fileName = extractFileName(p);

			JSONObject json = new JSONObject();        
			 
	        String sql = "INSERT INTO `files`(`user`, `filename`, `file`, `folder`) "+ 
	        		"VALUES (?,?,?,?);";
	        Connection con = DBConnectionHandler.getConnection();
	         
	        try {
	            PreparedStatement ps = con.prepareStatement(sql);
	            ps.setString(1, request.getParameter("user"));
	            ps.setString(2, fileName);
	            ps.setBlob(3, p.getInputStream());
	            ps.setString(4, request.getParameter("folder"));
	            int rs = ps.executeUpdate();
	            if (rs!=0) {
	                json.put("info", "success");
	            } else {
	                json.put("info", "fail");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        //System.out.println(json);
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(json.toString());
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
		
	}
	
	private String extractFileName(Part part) {
	    String contentDisp = part.getHeader("content-disposition");
	    String[] items = contentDisp.split(";");
	    for (String s : items) {
	        if (s.trim().startsWith("filename")) {
	            return s.substring(s.indexOf("=") + 2, s.length()-1);
	        }
	    }
	    return "";
	}

}
