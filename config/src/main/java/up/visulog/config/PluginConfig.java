package up.visulog.config;

import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

/**
 * Classe PluginConfig.
 * 
 * Une objet de type PluginConfig est caractérisé par les
 * informations suivantes :
 * 
 * Une Map pluginConfig attribuée .
 * 
 * @author Asma
 * @version : 1.0
*/

public class PluginConfig implements Serializable {

    /**
     * L'attribut pluginConfig de type Map.
     * 
     *@see PluginConfig#getPluginConfig()
     *
    */

    private Map<String, String> pluginConfig;
    
    public PluginConfig() {
    	this.pluginConfig = new HashMap<String, String>();
    }

     /**
     * Constructeur PluginConfig.
     * 
     * A la construction d'un objet PluginConfig.
     * 
     * @param Map La configuration unique d'un CountCommitPerAuthorPlugin.
     * 
     * @see pluginConfig#pluginConfig.
     * 
     */

	public PluginConfig(Map<String, String> pluginConfig) {
		this.pluginConfig = pluginConfig;
    }
    
   	public void setConfigOpt(String key, String value) {
   		this.pluginConfig.put(key, value);
   	}

   	public String getConfigOpt(String key) {
   		return this.pluginConfig.get(key);
   	}

	/**
     * Retourne le pluginConfig
     * 
     * @return pluginConfig.
     */
	public Map<String, String> getPluginConfig(){
		return this.pluginConfig;
	}
}
