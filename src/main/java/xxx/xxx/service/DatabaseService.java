package xxx.xxx.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import xxx.xxx.annotation.AutoWired;
import xxx.xxx.annotation.Service;
import xxx.xxx.model.Payload;

@Service
public class DatabaseService {

	private Logger logger = Logger.getLogger(DatabaseService.class.getName());

	@AutoWired
	private PropertyService propertyService;
	
	private static RocksDB db = null;

	@PostConstruct
	public void init() throws RocksDBException {

		try {

			RocksDB.loadLibrary();

			@SuppressWarnings("resource")
			Options options = new Options().setCreateIfMissing(true);

			String dbPath = propertyService.getDatabasePath();
			File directory = new File(dbPath);

			if (!directory.exists())
				directory.mkdirs();

			db = RocksDB.open(options, dbPath);
			
		} catch (Exception e) {

			logger.severe("Failure initializing database : " + e.getMessage());
			System.exit(1);
		}
	}

	public synchronized void delete(String key) throws RocksDBException {
		db.delete(key.getBytes());
	}

	public synchronized String get(String key) throws RocksDBException {
		return new String(db.get(key.getBytes()));
	}
	
	public boolean exists(String key) throws RocksDBException {
		return db.get(key.getBytes()) != null;
	}

	public synchronized void save(String key, String value) throws RocksDBException {
		db.put(key.getBytes(), value.getBytes());
	}
	
	public synchronized void update(String key, String value) throws RocksDBException {
		db.put(key.getBytes(), value.getBytes());
	}

	public synchronized JsonArray list() {

		JsonArray entries = new JsonArray();

		RocksIterator itr = db.newIterator();

		for (itr.seekToFirst(); itr.isValid(); itr.next()) {

			String key = new String(itr.key());
			String value = new String(itr.value());

			JsonObject json = new JsonObject();
			json.addProperty(key, value);

			entries.add(json);
		}
		
		return entries;
	}
}
