package edu.bupt.wangfu.info.msg.udp;

import java.io.Serializable;
import java.util.List;

public class Route implements Serializable {
	private static final long serialVersionUID = 1L;

	public String startSwtId;
	public String endSwtId;
	public List<String> route;//途经的swt的ID

//	private String startSwtId;
//	private String endSwtId;
//	public List<String> route;//途经的swt的ID

	public void setStartSwtId (String startSwtId){
		this.startSwtId = startSwtId;
	}

	public String getStartSwtId (){
		return startSwtId;
	}

	public void setEndSwtId (String endSwtId){
		this.endSwtId = endSwtId;
	}

	public String getEndSwtId (){
		return endSwtId;
	}

	public List<String> getRoute(){
		return route;
	}

	public void addRoute(String newRoute){
		route.add(newRoute);
	}

}