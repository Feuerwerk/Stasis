package de.boxxit.stasis;

import java.util.Map;
import com.esotericsoftware.kryo.Serializer;

/**
 * User: Christian Fruth
 */
public interface RemoteConnection
{
	public void setSynchronizer(Synchronizer synchronizer);
	public void setHandshakeHandler(HandshakeHandler handshakeHandler);
	@SuppressWarnings("rawtypes")
	public void setDefaultSerializer(Class<? extends Serializer> defaultSerializer);
	public void setCredentials(String userName, String password, Map<String, Object> parameters);

	public ConnectionState getState();
	public Map<String, Object> getParameters();
	public String getPassword();
	public String getUserName();

	public <T> void register(Class<T> type, int id);
	public <T> void register(Class<T> type, Serializer<T> serializer);
	public <T> void register(Class<T> type, Serializer<T> serializer, int id);

	public <T> void callAsync(CallHandler<T> handler, String name, Object... args);
	public <T> T callSync(String name, Object... args) throws Exception;
	public void login(CallHandler<Void> handler, String userName, String password, Map<String, Object> parameters);
}
