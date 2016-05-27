package com.worldpay.jalo;

import com.worldpay.constants.WorldpayaddonConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class WorldpayaddonManager extends GeneratedWorldpayaddonManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( WorldpayaddonManager.class.getName() );
	
	public static final WorldpayaddonManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (WorldpayaddonManager) em.getExtension(WorldpayaddonConstants.EXTENSIONNAME);
	}
	
}
