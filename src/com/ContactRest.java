package com;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import util.DBHelper;


//Contacts APP
@Path("/contact")
public class ContactRest {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/addContact")
	public Response addContact(String data) throws Exception{
		
		String status = "";
		String message = "";
		String finalResponse = "";
		
		try {
			
			
			JSONObject jsonObject = new JSONObject(data);
			System.out.println(jsonObject);
			
			String name = jsonObject.getString("name"); 
			String email = jsonObject.getString("email");
			
			if(emailExists(email)) {
				status = "failed";
				message = "Duplicate Email";
			}
			else {
				
				insertContact(name, email);
				status = "success";
				message = "Contact Added";
				
			}
			
			
			
		}catch(Exception e) {
			System.out.println("Exception Occured"+e);
		}
		
		finalResponse = "{\"status\":\""+status+"\",\"message\":\""+message+"\"}";
		return Response.ok(finalResponse).build();
	}
	
	
	public boolean emailExists(String email) throws Exception {
		
		Connection connection = DBHelper.getInstance().getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement("select * from contacts where email = ?");
		preparedStatement.setString(1, email);
		ResultSet resultSet = preparedStatement.executeQuery();
		
		if(resultSet.next()) {
			return true;
		}
		return false;
	}
	
	
	public void insertContact(String name,String email) throws Exception {
		
		Connection connection = DBHelper.getInstance().getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement("insert into contacts (name,email) values(?,?)");
		preparedStatement.setString(1, name);
		preparedStatement.setString(2, email);
		
		preparedStatement.execute();
		
	}
	
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/deleteContact")
	public Response deleteContact(String data) throws Exception{
		
		String status = "";
		String message = "";
		String finalResponse = "";
		
		try {
			JSONObject jsonObject = new JSONObject(data);
			String id = jsonObject.getString("id");
				
			
			
			Connection connection = DBHelper.getInstance().getConnection();
			
			PreparedStatement preparedStatement = connection.prepareStatement("select id from contacts where id = ?");
			preparedStatement.setString(1, id);
			
			if(preparedStatement.executeQuery()!=null) {
				 	
					preparedStatement = connection.prepareStatement("delete from contacts where id = ?");
					preparedStatement.setString(1, id);
					preparedStatement.execute();
						
					status = "success";
					message = "Contact Deleted";
			}else {
				
					status = "failed";
					message = "incorrect id";	
			}
		   
				
			
		}catch(Exception e) {
			
			status = "failed";
			message = "Exception Occured";
			
			System.out.println("Exception : "+e.getStackTrace());
			
		}
		
		finalResponse = "{\"status\":\""+status+"\",\"message\":\""+message+"\"}";
		return Response.ok(finalResponse).build();
		
		
		
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/searchContact")
	public Response searchContact(@QueryParam("search") String data,@QueryParam("page") String page) throws Exception{
		
		
		int pageSize = 5;
		System.out.println(data+","+page);
		
		Connection connection = DBHelper.getInstance().getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement("select name,email from contacts where name like ? or email like ? ");
		preparedStatement.setString(1, "%"+data+"%");
		preparedStatement.setString(2, "%"+data+"%");
		
		ResultSet resultSet = preparedStatement.executeQuery();
		
		JSONObject object = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		while(resultSet.next()) {
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", resultSet.getString(1));
			jsonObject.put("email", resultSet.getString(2));
			jsonArray.put(jsonObject);
			System.out.println(jsonArray);
			System.out.println(resultSet.getString(1)+","+resultSet.getString(2));
		}
		
		object.put("page", page);
		
		JSONArray jsonArray2 = null;
		
		if(page == null || page.equals("1") || page.equals("0")) {
			
			System.out.println("This Flow");
			
			jsonArray2 = new JSONArray();
			
			for(int i = 0 ; i < jsonArray.length() ; i ++) {
				
				if(i>=pageSize)
					break;
				
				JSONObject tempObject = jsonArray.getJSONObject(i);
				jsonArray2.put(tempObject);
			}
			
		}else {
			
			jsonArray2 = new JSONArray();
			
			for(int i = 0 ; i < jsonArray.length() ; i ++) {
				
				int tempPage = Integer.parseInt(page);
				System.out.println(tempPage);
				
				if(i >= tempPage*pageSize) {
					break;
				}
				
				if(i >= (tempPage-1) * pageSize) {
					
					JSONObject tempObject = jsonArray.getJSONObject(i);
					jsonArray2.put(tempObject);
					
				}
				
			}
			
			
		}
		
		
		
		object.put("data", jsonArray2);
		
		String finalResponse = object.toString();
		
		
		return Response.ok(finalResponse).build();
		
		
	}
	
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/editContact")
	public Response editContact(String data) throws Exception{
		
		String status = "";
		String message = "";
		String finalResponse = "";
		
		try {
			
			JSONObject jsonObject = new JSONObject(data);
			String id = jsonObject.getString("id");
			String name = jsonObject.getString("name");
			String email = jsonObject.getString("email");
			
			Connection connection = DBHelper.getInstance().getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement("select id from contacts where id = ?");
			preparedStatement.setString(1, id);
			
			if(preparedStatement.executeQuery()!=null) {
				
				updateContact(id,name, email);
				status = "success";
				message = "Contact Updated";
				
			}else {
				
				status = "failed";
				message = "No Such Contact";
				
			}
			
			
			
	}catch(Exception e) {
		
		status = "failed";
		message = "Exception Occured";
		e.printStackTrace();
		
	}
		
		finalResponse = "{\"status\":\""+status+"\",\"message\":\""+message+"\"}";
		return Response.ok(finalResponse).build();
		
	
	}
	
	
	public void updateContact(String id,String name,String email) throws Exception {
		
		Connection connection = DBHelper.getInstance().getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement("update contacts set name = ? , email = ? where id = ?");
		preparedStatement.setString(1, name);
		preparedStatement.setString(2, email);
		preparedStatement.setString(3, id);
		
		preparedStatement.execute();
		
	}
		
}
