preferences {
	input("locationname", "text", title: "Name of your neviweb® location", description: "Location name", required: true)
	input("devicename", "text", title: "Name of your neviweb® lightswitch", description: "Lightswitch name", required: true)
}

metadata {
	definition (name: "Sinopé Technologies Inc. Ligthswitch", namespace: "Sinopé Technologies Inc.", author: "Mathieu Virole") {
		capability "Switch"
		capability "Refresh"
		command "StartCommunicationWithServer"
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.Lighting.light11", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.Lighting.light13", backgroundColor:"#ffffff", nextState:"turningOn"
			}
		}

		standardTile("refresh", "device.power", inactiveLabel: false, decoration: "flat", width: 6, height: 2) {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		standardTile("error", "device.error", width: 6, height: 2) {
		    state "default", label:'${currentValue}', backgroundColor:"#ffffff", icon:"st.Office.office8"
		}

		main "switch"
		details(["switch","refresh", "error"])
	}
}

def initialize() {
}
def on() {
	def timeInSeconds = (Math.round(now()/1000))
	log.info("Turning ${device.name} \"ON\"")
	sendEvent(name: "switch", value: device.id+": "+timeInSeconds, state: "on", data: [deviceId: device.id, action: "on", evtTime: timeInSeconds])
}

def off() {
	def timeInSeconds = (Math.round(now()/1000))
	sendEvent(name: "switch", value:  device.id+": "+timeInSeconds, state: "off", data: [deviceId: device.id, action: "off", evtTime: timeInSeconds])
	log.info("Turning ${device.name} \"OFF\"")
}

def refresh(){
	def timeInSeconds = (Math.round(now()/1000))
	sendEvent(name: "switch", value:  device.id+": "+timeInSeconds, state: "refresh", data: [deviceId: device.id, action: "refresh", evtTime: timeInSeconds])
	log.info("Refreshing ${device.name}")
}

def StartCommunicationWithServer(data){
	// log.info "Action \"${data?.action}\" received from Service Manager with session [${data?.session}]"
	if( !state.deviceId || state.deviceId == true || state.deviceName != settings.devicename.toLowerCase().replaceAll("\\s", "") || state.locationName != settings.locationname.toLowerCase().replaceAll("\\s", "") ){
		state.deviceId = deviceId(data?.session)
	}
	def params = [
		path: "device/${state.deviceId}/attribute",
		headers: ['Session-Id' : data.session]
	]
	if(!state.deviceId){
		log.warn ("No device id found")
    	return sendEvent(name: 'error', value: "${error(1004)}")
	}else{
		switch(data.action){
			case "on":
				params.body = ['intensity' : 100]
				//params.headers['Content-Type'] = 'application/json'
				params.contentType = 'application/json'
				requestApi("setDevice", params);
				data.action = "refresh"
				StartCommunicationWithServer(data)
				break;
			case "off":
				params.body = ['intensity' : 0]
				params.contentType = 'application/json'
				requestApi("setDevice", params);
				data.action = "refresh"
				StartCommunicationWithServer(data)
				break;
			case "refresh":
				params.query = ['attributes' : 'intensity']
				// params.remove('body')
				requestApi("deviceData", params);
				break;
			default: 
				log.warn "invalide action"
		}
	}
}

def deviceId(session){
	data.deviceId = null
	locationId(session)
	if(data.locationId){
		def params = [
			uri: "${data.server}",
			path: "devices",
			requestContentType: "application/json, text/javascript, */*; q=0.01",
			headers: ['Session-Id' : session]
		]
		if(data?.locationId){
			params.query = ['location$id' : data.locationId]
		}

		requestApi("deviceList", params);

		def deviceName=settings.devicename
		if (deviceName!=null){
			deviceName=deviceName.toLowerCase().replaceAll("\\s", "")
		}
		data?.deviceId = null
		data.devices_list.each{var ->
			try{
				def name_device=var.name
				name_device=name_device.toLowerCase().replaceAll("\\s", "")
				if(name_device==deviceName){
					if(var.family=="2120" || var.family=="2120-1" || var.family=="2020" || var.family=="2020-1"){
						data.deviceId=var.id
						data.error=false
						state.deviceName = deviceName;
						state.deviceLocation = settings?.locationname.toLowerCase().replaceAll("\\s", "");
						return data.deviceId;
					}else{
						data.code=4002
					}
				}else{
					data.code=4001
				}
			}catch(e){
				data.code=4003
			}
		}
		if (!data?.deviceId || data.error){
			data.error=error(data.code)
			sendEvent(name: 'error', value: "${data.error}")
			data.deviceId=null;
			log.warn("${data.error}")
			data.error=true
		}
		else{
			data.deviceId
		}
		return data.deviceId
	}else{
		log.warn("${error(3001)}")
		return null;
	}
}

def locationId(session){
	def params = [
        path: "locations",
       	requestContentType: "application/json, text/javascript, */*; q=0.01",
        headers: ['Session-Id' : session]
    ]
    requestApi("locationList",params)
    def locationName=settings?.locationname
	if(locationName){
		locationName = locationName.toLowerCase().replaceAll("\\s", "")
	}
	data.locationId = null
	data.location_list.each{var ->    	
    	def name_location
		try{
			name_location = var.name
		}catch(e){
			log.error(var)
		}	
		if(name_location){
    		name_location = name_location.toLowerCase().replaceAll("\\s", "")
		}else{
			name_location = "INVALID LOCATION"
		}

    	if(name_location==locationName){
    		data.locationId = var.id
    		// log.info("Location ID is :: ${data.locationId}")
			state.locationName = locationName
    		data.error=null
    	}
    }
	
    if (!data.locationId){
    	sendEvent(name: 'error', value: "${error(3001)}")
    	data.error=true
    }
}

def isExpiredSessionEvent(resp){
	if( resp?.data?.error && resp?.data?.error?.code && resp?.data?.error?.code=="USRSESSEXP" ){
		sendEvent(name: "switch", value:  device.id+": "+timeInSeconds, state: "resetSession", data: [action: "resetSession", evtTime: timeInSeconds]);
	}
}

def requestApi(actionApi, params){
	params.uri = "https://smartthings.neviweb.com/"
	log.info("requestApi - ${actionApi}, -> ${params}");
	switch(actionApi){
		case "deviceList":
			httpGet(params) {resp ->
				isExpiredSessionEvent(resp)
				data.devices_list = resp.data
				
			}
		break;
		case "locationList":
			httpGet(params) {resp ->
				isExpiredSessionEvent(resp)
				data.location_list = resp.data
				
			}
		break;
		case "deviceData":
			httpGet(params) {resp ->
				isExpiredSessionEvent(resp)
				// log.info("Refresh API response [${resp.data}]")
				data.status = resp.data
				if (resp.data.errorCode == null){
    				sendEvent(name: 'error', value: "${error(0)}")
					if (resp.data.intensity==0){
						sendEvent(name: "switch", value: "off")
					}else{
						sendEvent(name: "switch", value: "on")
					}
				}else{
					data.error=error(resp.data.errorCode)
					sendEvent(name: 'error', value: "${data.error}")
					log.error("${data.error}")
				}
				return resp.data
			}
		break;
		case "setDevice":
			httpPut(params){resp -> 
				isExpiredSessionEvent(resp)
				// log.info("setDevice -> API response :: ${resp.data}")
			}
		break;
	}

}

def error(error){
	switch (error) {
		case 0: return ""
		case 1: return "Location name or Device name is wrong."
		case 100: return "Your session expired."
        case 1005: return "This action cannot be executed while in demonstration mode."
        case 1004: return "The resource you are trying to access could not be found."
        case 1003: return "You are not authorized to see this resource."
        case 1002: return "Wrong e-mail address or password. Please try again."
        case 1101: return "The e-mail you have entered is already used.  Please select another e-mail address."
        case 1102: return "The password you have provided is incorrect."
        case 1103: return "The password you have provided is not secure."
        case 1104: return "The account you are trying to log into is not activated. Please activate your account by clicking on the activation link located in the activation email you have received after registring. You can resend the activation email by pressing the following button."
        case 1105: return "Your account is disabled."
        case 1110: return "The maximum login retry has been reached. Your account has been locked. Please try again later."
        case 1111: return "Your account is presently locked. Please try again later."
        case 1120: return "The maximum simultaneous connections on the same IP address has been reached. Please try again later."
        case 2001: return "The device you are trying to access is temporarily unaccessible. Please try later."
        case 2002: return "The network you are trying to access is temporarily unavailable. Please try later."
        case 2003: return "The web interface (GT125) that you are trying to add is already present in your account."
        case 3001: return "Wrong location name. Please try again."
        case 4001: return "Wrong device name. Please try again."
        case 4002: return "This device is not Ligthswitch. Please change DeviceName."
        default: return "An error has occurred, please try again later."

    }
}
