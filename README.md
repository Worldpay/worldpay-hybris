# Installation and Usage

## Installing the Plugin into hybris with fulfilment functionality

First ensure that the version of hybris being used is supported for the plugin. Please view the Compatibility section for the current list of supported versions.

The plugin contains several hybris extensions. Take the following steps to include the full plugin into your hybris application:

1. Unzip the supplied plugin zip file

2. Copy the extracted folders to the ${HYBRIS_BIN_DIR} of your hybris installation.

3. Run the ‘ant clean’ command from within your hybris bin/platform directory.

4. Copy the following lines into your localextensions.xml after <path dir="${HYBRIS_BIN_DIR}"/>. The extensions do not rely on any absolute paths so it is also possible to place the extensions in a different location (such as ${HYBRIS_BIN_DIR}/custom).
- &lt;path autoload="true" dir="${HYBRIS_BIN_DIR}/ext-worldpay"/>
- &lt;path autoload="true" dir="${HYBRIS_BIN_DIR}/ext-worldpayfulfilment"/>

5. Run the following commands to install the AddOn's on the yaccelatorstorefront (replace "yacceleratorstorefront" with your custom storefront if relevant):

- ant addoninstall -Daddonnames="worldpayb2caddon,worldpaynotificationaddon" -DaddonStorefront.yacceleratorstorefront="yacceleratorstorefront"

### Optional

1. The worldpaysampledataaddon is optional, and can be installed by running:

- ant addoninstall -Daddonnames="worldpaysampledataaddon" -DaddonStorefront.yacceleratorstorefront="yacceleratorstorefront"

2. Run the ‘ant clean all’ command from within your hybris bin/platform directory.

3. Run the hybrisserver.sh to startup the hybris server.

4. Update your running system.using "ant updatesystem"

Except for setting up your hosts file, the Worldpay AddOn will work initally without any external setup needed.

The AddOn's are independent and can be installed on separate server instances. For example, the worldpaynotificationaddon AddOn can be running on server instances dedicated for listening to Worldpay Order Modification messages.

## Installing the Plugin into hybris with OMS functionality

First ensure that the version of hybris being used is supported for the plugin. Please view the Compatibility section for the current list of supported versions.

As the OMS extension cannot co-exist with the fulfilment extension (i.e. any fulfilment process generated through modulegen - yfulfilmentprocess), if the functionality has been extended, all customisations will need to be applied in the OMS extension and the fulfilment extension removed from the installation.

The plugin is supplied as a zip file with several hybris extensions inside. Take the following steps to include the full plugin into your hybris application:

1. Unzip the supplied plugin zip file

2. Copy the extracted folders to the ${HYBRIS_BIN_DIR} of your hybris installation.

3. Run the ‘ant clean’ command from within your hybris bin/platform directory.

4. Copy the following lines into your localextensions.xml after <path dir="${HYBRIS_BIN_DIR}"/>. The extensions do not rely on any absolute paths so it is also possible to place the extensions in a different location (such as ${HYBRIS_BIN_DIR}/custom).

- &lt;path autoload="true" dir="${HYBRIS_BIN_DIR}/ext-worldpay"/>

- &lt;path autoload="true" dir="${HYBRIS_BIN_DIR}/ext-worldpayoms"/>

5. Run the following commands to install the AddOn's on the yaccelatorstorefront (replace "yacceleratorstorefront" with your custom storefront if relevant):

- ant addoninstall -Daddonnames="worldpayb2caddon,worldpaynotificationaddon,ordermanagementaddon" -DaddonStorefront.yacceleratorstorefront="yacceleratorstorefront"

### Optional

1. The worldpaysampledataaddon is optional, and can be installed by running:

- ant addoninstall -Daddonnames="worldpaysampledataaddon" -DaddonStorefront.yacceleratorstorefront="yacceleratorstorefront"

2. Run the ‘ant clean all’ command from within your hybris bin/platform directory.

3. Run the hybrisserver.sh to startup the hybris server.

4. Update your running system.using "ant updatesystem"

Except for setting up your hosts file, the Worldpay AddOn will work initally without any external setup needed.

The AddOn's are independent and can be installed on separate server instances. For example, the worldpaynotificationaddon AddOn can be running on server instances dedicated for listening to Worldpay Order Modification messages.

## Installing the Plugin using the provided recipes

The AddOn provides 3 gradle recipes to be used with the hybris installer.
1. wp_b2c_acc with fulfilment functionality
2. wp_b2c_acc_oms with OMS functionality.
3. wp_b2b_acc that uses the B2B accelerator

The recipes are based on the b2c_acc, b2c_acc_oms and b2b_acc recipes provided by hybris.

The recipes can be found under the installer folder.

To use the recipes on a clean hybris installation, copy the folder hybris to your ${HYBRIS_BIN_DIR}

Since the recipe generates the local.properties file with the properties defined in the recipe, optionally you can add your local.properties to the customconfig folder.

In order to install the AddOn using one of the recipes, run the following commands:
- This will create a solution from the accelerator templates, and install the addons.
HYBRIS_HOME/installer$ ./install.sh -r [RECIPE_NAME] setup
- This will build and initialize the platform
HYBRIS_HOME/installer$ ./install.sh -r [RECIPE_NAME] initialize
- This will start a commerce suite instance
HYBRIS_HOME/installer$ ./install.sh -r [RECIPE_NAME] start
