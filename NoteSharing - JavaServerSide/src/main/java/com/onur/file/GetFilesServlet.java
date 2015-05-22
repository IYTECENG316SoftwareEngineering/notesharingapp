package com.onur.file;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.onur.database.DBConnectionHandler;

/**
 * Servlet implementation class GetFilesServlet
 */
public class GetFilesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//response.setContentType("text/html;charset=UTF-8");
        //JSONObject json = new JSONObject();
		String folder = request.getParameter("folder");
        String[] item = new String[50];
        String username, filename;
        Blob file;
        int i=0;
        List<String> list = new ArrayList<String>();
        String json = ""; 
        String sql = "SELECT `id`, `user`, `filename`, `file` FROM `files` WHERE `folder`='" + folder + "' ORDER BY `id`";
        Connection con = DBConnectionHandler.getConnection();
        
        try {
            Statement ps = con.createStatement();
            ResultSet rs = ps.executeQuery(sql);
            while (rs.next()) {
            	String id  = rs.getString("id");
                username = rs.getString("user");
                filename = rs.getString("filename");
                file = rs.getBlob("file");
                item[i] = id + " (" + username + ") - " + filename;
                list.add(item[i]);
                json = new Gson().toJson(list);
                //json.put("id", id);
                //json.put("item", item[i]);
                i++;
                
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(json);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
 
    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
