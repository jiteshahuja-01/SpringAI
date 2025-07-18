package com.example.SpringAI.service;

import com.example.SpringAI.model.Chunk;
import com.example.SpringAI.model.RedisSearchCommand;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.search.Document;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.SearchResult;
import redis.clients.jedis.util.SafeEncoder;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class RedisVectorService {

    @Autowired
    private JedisPool jedisPool;

    @PostConstruct
    public void init() {
        createIndexIfNotExists();
    }


    public void storeChunk(String docId, int chunkIndex, String chunk, float[] embedding) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "doc:" + docId + ":" + chunkIndex;
            Map<String, Object> hash = new HashMap<>();
            hash.put("chunk", chunk);
            hash.put("embedding", toBytes(embedding));

            jedis.hset(key.getBytes(), toByteMap(hash));
        }
    }

    private Map<byte[], byte[]> toByteMap(Map<String, Object> map) {
        Map<byte[], byte[]> result = new HashMap<>();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            byte[] key = e.getKey().getBytes(StandardCharsets.UTF_8);
            byte[] value = (e.getValue() instanceof byte[]) ? (byte[]) e.getValue() : e.getValue().toString().getBytes();
            result.put(key, value);
        }
        return result;
    }


    public List<Chunk> searchSimilarChunks(float[] queryVector) {
        System.out.println("Query vector length: " + queryVector.length);
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] vectorBlob = toBytes(queryVector);
            System.out.println("Query Vector Size in bytes : " + vectorBlob.length);
            String base64 = Base64.getEncoder().encodeToString(vectorBlob);
            // Build raw FT.SEARCH command with KNN
            List<byte[]> args = new ArrayList<>();
            //args.add(SafeEncoder.encode("FT.SEARCH"));
            args.add(SafeEncoder.encode("doc_index")); // Index name
            args.add(SafeEncoder.encode("*=>[KNN 5 @embedding $vec_param AS score]")); // Query with KNN
            args.add(SafeEncoder.encode("PARAMS"));
            args.add(SafeEncoder.encode("2"));
            args.add(SafeEncoder.encode("vec_param"));
            args.add(vectorBlob); // vector as raw byte[]
            args.add(SafeEncoder.encode("SORTBY"));
            args.add(SafeEncoder.encode("score"));
            args.add(SafeEncoder.encode("ASC")); // or DESC
            args.add(SafeEncoder.encode("RETURN"));
            args.add(SafeEncoder.encode("1"));
            args.add(SafeEncoder.encode("chunk"));
            args.add(SafeEncoder.encode("DIALECT"));
            args.add(SafeEncoder.encode("2"));

            Object rawResponse = jedis.sendCommand(RedisSearchCommand.FT_SEARCH, args.toArray(new byte[0][]));

            return parseRedisSearchResponse(rawResponse);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Chunk> parseRedisSearchResponse(Object response) {
        List<Chunk> results = new ArrayList<>();
        if (!(response instanceof List)) return results;

        List<Object> list = (List<Object>) response;
        for (int i = 1; i < list.size(); i += 2) { // skip first element (total count)
            Map<String, Object> fields = parseFields((List<Object>) list.get(i + 1));
            String text = new String((byte[]) fields.get("chunk"));
            results.add(new Chunk(text));
        }
        return results;
    }

    private Map<String, Object> parseFields(List<Object> flatList) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < flatList.size(); i += 2) {
            String key = new String((byte[]) flatList.get(i));
            Object value = flatList.get(i + 1);
            map.put(key, value);
        }
        return map;
    }


    public void createIndexIfNotExists() {
        if (doesIndexExist("doc_index")) return;

        try (Jedis jedis = jedisPool.getResource()) {
            List<byte[]> args = new ArrayList<>();

            args.add(SafeEncoder.encode("doc_index"));
            args.add(SafeEncoder.encode("ON"));
            args.add(SafeEncoder.encode("HASH"));
            args.add(SafeEncoder.encode("PREFIX"));
            args.add(SafeEncoder.encode("1"));
            args.add(SafeEncoder.encode("doc:"));

            args.add(SafeEncoder.encode("SCHEMA"));

            args.add(SafeEncoder.encode("chunk"));
            args.add(SafeEncoder.encode("TEXT"));

            args.add(SafeEncoder.encode("embedding"));
            args.add(SafeEncoder.encode("VECTOR"));
            args.add(SafeEncoder.encode("HNSW"));
            args.add(SafeEncoder.encode("6"));
            args.add(SafeEncoder.encode("TYPE"));
            args.add(SafeEncoder.encode("FLOAT32"));
            args.add(SafeEncoder.encode("DIM"));
            args.add(SafeEncoder.encode("768"));
            args.add(SafeEncoder.encode("DISTANCE_METRIC"));
            args.add(SafeEncoder.encode("COSINE"));

// REMOVE the problematic INITIAL_CAP, EF_RUNTIME, etc. for now

            jedis.sendCommand(RedisSearchCommand.FT_CREATE, args.toArray(new byte[0][]));


            System.out.println("✅ RediSearch index 'doc_index' created.");
        }
    }



    public boolean doesIndexExist(String indexName) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.sendCommand(RedisSearchCommand.FT_INFO, indexName);
            return true;
        } catch (JedisDataException e) {
            // If index doesn't exist, Redis throws an error
            if (e.getMessage().toLowerCase().contains("unknown index name")) {
                return false;
            }
            throw e; // Unexpected error
        }
    }


    private byte[] toBytes(float[] vector) {
        ByteBuffer buffer = ByteBuffer.allocate(vector.length * 4);
        for (float v : vector) buffer.putFloat(v);
        return buffer.array();
    }
}
