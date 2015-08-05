package toberumono.json;

/**
 * Describes the methods common to {@link JSONData} types that can track modifications to their contents.
 * 
 * @author Toberumono
 * @see JSONObject
 * @see JSONArray
 */
public interface ModifiableJSONData {
	
	/**
	 * Checks if the {@link ModifiableJSONData} or any of its descendants have been modified.
	 * 
	 * @return {@code true} if the {@link ModifiableJSONData} has been modified
	 */
	public boolean isModified();
	
	/**
	 * Clears the modified flag for the {@link ModifiableJSONData} and all of its descendants.
	 */
	public void clearModified();
}
