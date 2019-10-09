
# SmartThings Public Github Repo

<ul>
	<li><a href="#driver" title="Titre">Handlers / Pilotes</a>
		<ul>
			<li><a href="#Ten" title="Titre">English</a></li>
			<li><a href="#Tfr" title="Titre">Francais</a></li>
		</ul>
	</li>
	<li><a href="#sw" title="Titre">Add devices / Ajout d'appareils</a>
		<ul>
			<li><a href="#Sen" title="Titre">English</a></li>
			<li><a href="#Sfr" title="Titre">Francais</a></li>
		</ul>
	</li>
</ul>




<h2 id="driver">Handlers</h2>
<h3 id="Ten">Driver installation STEP BY STEP: </h3>


1. Log on : [smartthings API](https://graph.api.smartthings.com/ide/devices)

2. Click on "My Device Handlers"

	![Image device Handlers](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/deviceHandler.PNG)

3. Click on "Create New Device Handler"

4. Click on "From Code" and copy one of those handler code :
	* [Thermostat handler](https://github.com/sinopetechnologies/smartThings/blob/master/driver_device_thermostat.groovy)
	* [Lightswitch handler](https://github.com/sinopetechnologies/smartThings/blob/master/driver_device_lightswitch.groovy),
	* [Dimmer handler](https://github.com/sinopetechnologies/smartThings/blob/master/driver_device_dimmer.groovy),
	* [Load controller handler](https://github.com/sinopetechnologies/smartThings/blob/master/driver_device_loadcontroller.groovy)

	![Image code smartthings](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/copy_code.PNG)

5. Click on "Create"

6. "Save" and click on "publish for me" 

	![Image save and publish](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/save%26publish.PNG)

7. Repeat steps 3 to 6 for every needed handler

8. Click on "My SmartApps" and "New SmartApp"

	![Image change type](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/createSmartApp.PNG)

9. Click on "From Code" and copy [the service manager code](https://github.com/sinopetechnologies/smartThings/blob/master/service_app.groovy)

	![Image code smartthings](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/codeSmartApp.PNG)

10. Click on "Create"


<h2 id="Tfr">Installer les pilotes étape par étape : </h2>


1. Connectez vous sur : [l' API de Smartthings ](https://graph.api.smartthings.com/ide/devices)

2. Appuyez sur " My Device Handlers "

	![Image device Handlers](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/deviceHandler.PNG)

3. Appuyez sur " Create New Device Handler "

4. Appuyez sur " From Code " et copiez le code de l'un de ces pilotes :
	* [Pilote de thermostat](https://github.com/sinopetechnologies/smartThings/blob/master/driver_device_thermostat.groovy)
	* [Pilote d'interrupteur](https://github.com/sinopetechnologies/smartThings/blob/master/driver_device_lightswitch.groovy),
	* [Pilote de  gradateur](https://github.com/sinopetechnologies/smartThings/blob/master/driver_device_dimmer.groovy),
	* [Pilote de contrôleur de charge](https://github.com/sinopetechnologies/smartThings/blob/master/driver_device_loadcontroller.groovy)

	![Image code smartthings](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/copy_code.PNG)

5. Appuyez sur " Create "

6. Appuyez sur " Save " et " publish for me " 

	![Image save and publish](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/save%26publish.PNG)

7. Répéter les étapes 3 à 6 pour les différents pilotes nécessaire

8. Appuyez sur "My SmartApps" and on "New SmartApp"

	![Image change type](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/createSmartApp.PNG))

9. Appuyez sur "From Code" et copiez [le code du service manager](https://github.com/sinopetechnologies/smartThings/blob/master/service_app.groovy)

	![Image code smartthings](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/codeSmartApp.PNG))

10. Appuyez sur "Create"















==========================================================================================================================================================
==========================================================================================================================================================

<h2 id="sw">Add devices</h2>
<h3 id="Sen">Add a device STEP BY STEP: </h3>


1. Log on : [smartthings API](https://graph.api.smartthings.com/ide/devices)

2. Click on "My Devices" and "New Device"

	![Image device & New Device](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/new_device.PNG)

3. Fill in the form and change type for one of the following at the end of the list:
	* Sinopé Technologies Inc. Dimmer
	* Sinopé Technologies Inc. Lightswitch
	* Sinopé Technologies Inc. loadController
	* Sinopé Technologies Inc. Thermostat

	Fill "Device Network Id" with random text. This field is mandatory for SmartThings, but it's never used.

	![Image change type](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/type.PNG)

4. Connect to your SmartThings phone app

5. Click on "Add a SmartApp" under Automation/SmartApps
	If you already have a Sinopé Technologies Inc. service manager, click on it and go to step 8.

	![Image code smartthings](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/addSmartApp-1.png)

6. Click on "My Apps"

	![Image code smartthings](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/addSmartApp-2.png)

7. Click on "Sinopé Technologies Inc. service manage"

	![Image code smartthings](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/addSmartApp-3.png)

8. Fill in the form. Update your selected device list whenever you add a new device.

	![Image code smartthings](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/formSmartApp.png)

9. Click on your Device create in step 3

10. Click on parameter

	![Image parameter button](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/apps_sinope.png)

11. Fill in the form

	The device name must be unique for its location.

	![Image forms](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/forms.png)

12. Click on "Done"


<h2 id="Sfr">Ajouter un appareil étape par étape : </h2>


1. Connectez vous sur : [l' API de Smartthings ](https://graph.api.smartthings.com/ide/devices)

2. Appuyez sur " My Devices " et " New Device "

	![Image device & New Device](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/new_device.PNG)

3. Complétez le formulaire et changer le type pour le champ correspondant. Il se trouve en fin de liste.
	* Pour les gradateur : Sinopé Technologies Inc. Dimmer
	* Pour les interrupteur : Sinopé Technologies Inc. Lightswitch
	* Pour les contrôleur de charge : Sinopé Technologies Inc. loadController
	* Pour les thermostat : Sinopé Technologies Inc. Thermostat

	Complétez "Device Network Id" avec un texte aléatoire. Ce champs est obligatoire pour SmartThings, mais il n'est jamais utilisé.

	![Image change type](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/type.PNG)

4. Connectez vous sur l'application SmartThings de votre téléphone

5. Appuyez sur "Add a SmartApp" sous Automation/SmartApps
	Si vous possédez déjà un smartApps Sinopé Technologies Inc. service manager, appuyez sur celui-ci puis allez à l'étape 8.

	![Image code smartthings](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/addSmartApp-1.png)

6. Appuyez sur "My Apps"

	![Image code smartthings](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/addSmartApp-2.png)

7. Appuyez sur "Sinopé Technologies Inc. service manage"

	![Image code smartthings](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/addSmartApp-3.png)

8. Complétez le formulaire. Assurez vous de mettre à jour la liste des appareils sélectionné à chaque fois que vous en ajoutez un nouveau.

	![Image code smartthings](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/formSmartApp.png)

10. Appuyez sur l'appareil que vous avez crée en étape 3.

11. Appuyez sur paramètre

	Le nom de l'appareil doit être unique pour son emplacement.

	![Image parameter button](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/apps_sinope.png)

12. Complétez le formulaire

	![Image forms](https://raw.githubusercontent.com/sinopetechnologies/pictures_readme/master/forms.png)

13. Appuyez sur " Done "
