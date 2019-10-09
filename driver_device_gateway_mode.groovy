preferences {
	input("locationname", "text", title: "Name of your neviweb® location", description: "Location name", required: true)
}

metadata {
	definition (name: "Sinopé Technologies Inc. Gateway Mode", namespace: "Sinopé Technologies Inc.", author: "Mathieu Virole, Antoine Guilbaud") {
		capability "Switch"
		capability "Refresh"
		command "StartCommunicationWithServer"
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:"home", action:"switch.off", icon:"st.Home.home2", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "off", label:"away", action:"switch.on", icon:"st.Home.home2", backgroundColor:"#ffffff", nextState:"turningOn"
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
	def locId = (locationId(data?.session))

	if (state.locationId == null)
	{
		log.error("INVALID LOCATION")
		return;
	}
	
	def params = [
		path: "location/$state.locationId/mode",
		headers: ['Session-Id' : data.session]
	]

	switch(data.action){
		case "on":
			params.body = ['mode' : 'home']
			//params.headers['Content-Type'] = 'application/json'
			params.contentType = 'application/json'
			requestApi("setDevice", params);
			data.action = "refresh"
			StartCommunicationWithServer(data)
			break;
		case "off":
			params.body = ['mode' : 'away']
			params.contentType = 'application/json'
			requestApi("setDevice", params);
			data.action = "refresh"
			StartCommunicationWithServer(data)
			break;
		case "refresh":
			params.query = ['attributes' : 'mode']
			requestApi("deviceData", params);
			break;
		default: 
			log.warn "invalid action"
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
    		//log.info("Location ID is :: ${data.locationId}")
			state.locationName = locationName
			state.locationId = var.id
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
					if (resp.data.mode=='away'){
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
			httpPost(params){resp -> 
				isExpiredSessionEvent(resp)
				//log.info("setDevice -> API response :: ${resp.data}")
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
