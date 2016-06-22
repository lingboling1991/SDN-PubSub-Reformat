package edu.bupt.wangfu.webservice;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * Created by lenovo on 2016-6-22.
 */

@WebService(targetNamespace = "http://edu.bupt.wangfu.handlers.webservice", name = "WsnProcess")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface WsnProcess {
	String wsnProcess(@WebParam(partName = "Wsn", name = "WsnProcess", targetNamespace = "http://edu.bupt.wangfu.handlers.webservice") String message);
}
