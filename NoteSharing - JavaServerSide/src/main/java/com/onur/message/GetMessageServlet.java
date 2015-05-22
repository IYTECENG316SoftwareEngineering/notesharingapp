package com.onur.message;

import com.google.gson.Gson;
import com.onur.database.DBConnectionHandler;
import static java.lang.System.out;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
 
public class GetMessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //response.setContentType("text/html;charset=UTF-8");
        //JSONObject json = new JSONObject();
        String[] item = new String[50];
        String username, message;
        int i=0;
        List<String> list = new ArrayList<String>();
        String json = ""; 
        String sql = "SELECT `id`, `user`, `message` FROM `messages` ORDER BY `id`";
        Connection con = DBConnectionHandler.getConnection();
        
        try {
            Statement ps = con.createStatement();
            ResultSet rs = ps.executeQuery(sql);
            while (rs.next()) {
            	String id  = rs.getString("id");
                username = rs.getString("user");
                message = rs.getString("message");
                item[i] = id + " (" + username + ") - " + message;
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
