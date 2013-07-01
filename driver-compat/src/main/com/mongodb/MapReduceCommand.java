/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb;

import org.bson.types.Code;
import org.mongodb.Document;
import org.mongodb.command.Command;
import org.mongodb.operation.MapReduce;
import org.mongodb.operation.MapReduceOutput;

import java.util.Map;

import static com.mongodb.DBObjects.toDocument;

/**
 * This class groups the argument for a map/reduce operation and can build the underlying command object
 *
 * @mongodb.driver.manual applications/map-reduce Map-Reduce
 */
public class MapReduceCommand {

    private final String mapReduce;
    private final String map;
    private final String reduce;
    private String finalize;
    private ReadPreference readPreference;
    private final OutputType outputType;
    private final String outputCollection;
    private String outputDB;
    private final DBObject query;
    private DBObject sort;
    private int limit;
    private Map<String, Object> scope;
    private Boolean verbose;
    private DBObject extraOptions;

    /**
     * Represents the command for a map reduce operation Runs the command in REPLACE output type to a named collection
     *
     * @param inputCollection  collection to use as the source documents to perform the map reduce operation.
     * @param map              a JavaScript function that associates or “maps” a value with a key and emits the key and value pair.
     * @param reduce           a JavaScript function that “reduces” to a single object all the values associated with a particular key.
     * @param outputCollection optional - leave null if want to get the result inline
     * @param type             the type of output
     * @param query            specifies the selection criteria using query operators for determining the documents input to the map function.
     */
    public MapReduceCommand(final DBCollection inputCollection, final String map, final String reduce, final String outputCollection,
                            final OutputType type, final DBObject query) {
        this.mapReduce = inputCollection.getName();
        this.map = map;
        this.reduce = reduce;
        this.outputCollection = outputCollection;
        this.outputType = type;
        this.query = query;
        this.outputDB = null;
        this.verbose = true;
    }

    /**
     * Sets the verbosity of the MapReduce job, defaults to 'true'
     *
     * @param verbose The verbosity level.
     */
    public void setVerbose(final Boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Gets the verbosity of the MapReduce job.
     *
     * @return the verbosity level.
     */
    public Boolean isVerbose() {
        return verbose;
    }

    /**
     * Get the name of the collection the MapReduce will read from
     *
     * @return name of the collection the MapReduce will read from
     */
    public String getInput() {
        return mapReduce;
    }


    /**
     * Get the map function, as a JS String
     *
     * @return the map function (as a JS String)
     */
    public String getMap() {
        return map;
    }

    /**
     * Gets the reduce function, as a JS String
     *
     * @return the reduce function (as a JS String)
     */
    public String getReduce() {
        return reduce;
    }

    /**
     * Gets the output target (name of collection to save to) This value is nullable only if OutputType is set to
     * INLINE
     *
     * @return The outputCollection
     */
    public String getOutputTarget() {
        return outputCollection;
    }


    /**
     * Gets the OutputType for this instance.
     *
     * @return The outputType.
     */
    public OutputType getOutputType() {
        return outputType;
    }


    /**
     * Gets the Finalize JS Function
     *
     * @return The finalize function (as a JS String).
     */
    public String getFinalize() {
        return finalize;
    }

    /**
     * Sets the Finalize JS Function
     *
     * @param finalize The finalize function (as a JS String)
     */
    public void setFinalize(final String finalize) {
        this.finalize = finalize;
    }

    /**
     * Gets the query to run for this MapReduce job
     *
     * @return The query object
     */
    public DBObject getQuery() {
        return query;
    }

    /**
     * Gets the (optional) sort specification object
     *
     * @return the Sort DBObject
     */
    public DBObject getSort() {
        return sort;
    }

    /**
     * Sets the (optional) sort specification object
     *
     * @param sort The sort specification object
     */
    public void setSort(final DBObject sort) {
        this.sort = sort;
    }

    /**
     * Gets the (optional) limit on input
     *
     * @return The limit specification object
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Sets the (optional) limit on input
     *
     * @param limit The limit specification object
     */
    public void setLimit(final int limit) {
        this.limit = limit;
    }

    /**
     * Gets the (optional) JavaScript  scope
     *
     * @return The JavaScript scope
     */
    public Map<String, Object> getScope() {
        return scope;
    }

    /**
     * Sets the (optional) JavaScript scope
     *
     * @param scope The JavaScript scope
     */
    public void setScope(final Map<String, Object> scope) {
        this.scope = scope;
    }

    /**
     * Sets the (optional) database name where the output collection should reside
     *
     * @param outputDB
     */
    public void setOutputDB(final String outputDB) {
        this.outputDB = outputDB;
    }

    public DBObject toDBObject() {
        final BasicDBObject cmd = new BasicDBObject();

        cmd.put("mapreduce", mapReduce);
        cmd.put("map", map);
        cmd.put("reduce", reduce);
        cmd.put("verbose", verbose);

        final BasicDBObject out = new BasicDBObject();
        switch (outputType) {
            case INLINE:
                out.put("inline", 1);
                break;
            case REPLACE:
                out.put("replace", outputCollection);
                break;
            case MERGE:
                out.put("merge", outputCollection);
                break;
            case REDUCE:
                out.put("reduce", outputCollection);
                break;
            default:
                throw new IllegalArgumentException("Unexpected output type");
        }
        if (outputDB != null) {
            out.put("db", outputDB);
        }
        cmd.put("out", out);

        if (query != null) {
            cmd.put("query", query);
        }

        if (finalize != null) {
            cmd.put("finalize", finalize);
        }

        if (sort != null) {
            cmd.put("sort", sort);
        }

        if (limit > 0) {
            cmd.put("limit", limit);
        }

        if (scope != null) {
            cmd.put("scope", scope);
        }

        if (extraOptions != null) {
            cmd.putAll(extraOptions);
        }

        return cmd;
    }

    public void addExtraOption(final String name, final Object value) {
        if (extraOptions == null) {
            extraOptions = new BasicDBObject();
        }
        extraOptions.put(name, value);
    }

    public DBObject getExtraOptions() {
        return extraOptions;
    }

    /**
     * Sets the read preference for this command. See the * documentation for {@link ReadPreference} for more
     * information.
     *
     * @param preference Read Preference to use
     */
    public void setReadPreference(final ReadPreference preference) {
        this.readPreference = preference;
    }

    /**
     * Gets the read preference
     *
     * @return
     */
    public ReadPreference getReadPreference() {
        return readPreference;
    }


    public String toString() {
        return toDBObject().toString();
    }

    /**
     * Type of the output:
     * <ul>
     * <li>INLINE - Return results inline, no result is written to the DB server</li>
     * <li>REPLACE - Save the job output to a collection, replacing its previous content</li>
     * <li>MERGE - Merge the job output with the existing contents of outputCollection collection</li>
     * <li>REDUCE - Reduce the job output with the existing contents of outputCollection collection</li>
     * </ul>
     */
    public static enum OutputType {
        REPLACE,
        MERGE,
        REDUCE,
        INLINE
    }

    public Command toNew() {
        final MapReduce mapReduce;
        if (outputType == OutputType.INLINE) {
            mapReduce = new MapReduce(new Code(map), new Code(reduce));
        } else {
            final MapReduceOutput output = new MapReduceOutput(outputCollection)
                    .database(outputDB);
            switch (outputType) {
                case MERGE:
                    output.action(MapReduceOutput.Action.MERGE);
                    break;
                case REDUCE:
                    output.action(MapReduceOutput.Action.REDUCE);
                    break;
                case REPLACE:
                    output.action(MapReduceOutput.Action.REPLACE);
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected action on target collection");
            }
            //TODO: sharded?
            //TODO: non-atomic?
            mapReduce = new MapReduce(new Code(map), new Code(reduce), output);
        }

        if (query != null) {
            mapReduce.filter(toDocument(query));
        }

        if (finalize != null) {
            mapReduce.finalize(new Code(finalize));
        }

        if (sort != null) {
            mapReduce.sort(toDocument(sort));
        }

        mapReduce.limit(limit);

        if (scope != null) {
            mapReduce.scope(new Document(scope));
        }
        if (verbose) {
            mapReduce.verbose();
        }
        //TODO: jsMode?

        return new org.mongodb.command.MapReduce(mapReduce, this.mapReduce);

    }
}