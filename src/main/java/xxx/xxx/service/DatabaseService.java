package xxx.xxx.service;

import java.io.File;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import xxx.xxx.annotation.Service;

@Service
public class DatabaseService {

	private Logger logger = Logger.getLogger(DatabaseService.class.getName());
	private RocksDB db;
	
	private String dbPath = "/home/glmars3/Desktop/database";
	
	@PostConstruct
	public void init() throws RocksDBException {
		
		try {
			
			RocksDB.loadLibrary();
			
			@SuppressWarnings("resource")
			Options options = new Options().setCreateIfMissing(true);
			
			File file = new File(dbPath);
			
			if(!file.exists())
				file.mkdirs();
			
			db = RocksDB.open(options, dbPath);
		}
		catch(Exception e) {
			
			logger.severe("Failure initializing database : "+ e.getMessage());
			System.exit(1);
		}
	}

	public boolean delete(String key) {
		
		try {
			
			db.delete(key.getBytes());
		}
		catch(Exception e) {
			
			return false;
		}
		
		return true;
	}
	
	public String getEntry(String key) throws Exception {
		
		return new String(db.get(key.getBytes()));
	}
	
	public boolean saveEntry(String key, String value) {
		
		try {
			
			db.put(key.getBytes(), value.getBytes());
		}
		catch(Exception e) {
			
			return false;
		}
		
		return true;
	}
}
