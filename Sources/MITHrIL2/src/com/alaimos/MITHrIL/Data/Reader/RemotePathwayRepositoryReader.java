package com.alaimos.MITHrIL.Data.Reader;

import com.alaimos.Commons.Reader.AbstractRemoteDataReader;
import com.alaimos.Commons.Reader.RemoteDataReaderInterface;
import com.alaimos.MITHrIL.Data.Pathway.Factory.PathwayFactory;
import com.alaimos.MITHrIL.Data.Pathway.Interface.*;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 12/12/2015
 */
public class RemotePathwayRepositoryReader extends AbstractRemoteDataReader<RepositoryInterface> {

    private static final String SEPARATOR        = "\t";
    private static final String LIST_SEPARATOR   = ";";
    private static final String OTHER_SEPARATOR  = ",";
    private static final String PATHWAYS_HEADER  = "# Pathways";
    private static final String NODES_HEADER     = "# Nodes";
    private static final String EDGES_HEADER     = "# Edges";
    private static final String ENDPOINTS_HEADER = "# Endpoints";

    private PathwayFactoryInterface        pf       = PathwayFactory.getInstance();
    private HashMap<String, NodeInterface> allNodes = new HashMap<>();

    public RemotePathwayRepositoryReader(String url) {
        setPersisted(true).setUrl(url);
    }

    @Override
    public RemoteDataReaderInterface<RepositoryInterface> setUrl(String url) {
        super.setUrl(url);
        setFile("pathway-repository-" + FilenameUtils.getName(url));
        return this;
    }

    private PathwayInterface readPathway(String line) {
        String[] sLine = line.split(SEPARATOR, -1);
        if (sLine.length != 5 || sLine[0].trim().isEmpty() || sLine[1].trim().isEmpty()) {
            throw new RuntimeException("Incorrect pathway line format -->" + line + "<--.");
        }
        PathwayInterface p = pf.getPathway(sLine[0].trim(), sLine[1].trim(), pf.getGraph(), sLine[4].trim())
                               .setImage(sLine[2].trim()).setUrl(sLine[3].trim());
        p.getGraph().setOwner(p);
        return p;
    }

    private NodeInterface readNode(String line) {
        String[] sLine = line.split(SEPARATOR, -1);
        if ((sLine.length != 3 && sLine.length != 4) || sLine[0].trim().isEmpty()) {
            throw new RuntimeException("Incorrect node line format -->" + line + "<--.");
        }
        return pf.getNode(sLine[0].trim(), sLine[1].trim(), (sLine.length != 4) ? "other" : sLine[3].trim())
                 .setAliases(Arrays.stream(sLine[2].split(LIST_SEPARATOR)).map(String::trim).filter(s -> !s.isEmpty())
                                   .collect(Collectors.toList()));
    }

    @Nullable
    private EdgeInterface readEdge(String line, RepositoryInterface repository) {
        String[] sLine = line.split(SEPARATOR, -1);
        if (sLine.length != 5) {
            throw new RuntimeException("Incorrect edge line format -->" + line + "<--.");
        }
        if (Arrays.stream(sLine).mapToInt(s -> ((s.trim().isEmpty()) ? 1 : 0)).sum() > 0) {
            throw new RuntimeException("Incorrect edge line format -->" + line + "<--.");
        }
        NodeInterface start = allNodes.get(sLine[0].trim()),
                end = allNodes.get(sLine[1].trim());
        PathwayInterface owner = repository.getPathwayById(sLine[4].trim());
        if (start == null || end == null) return null;
        return pf.getEdge(start, end, sLine[2].trim(), sLine[3].trim(), owner);
    }

    private void readEndpoints(String line, RepositoryInterface repository) {
        String[] sLine = line.split(SEPARATOR, -1);
        if (sLine.length != 2) {
            throw new RuntimeException("Incorrect endpoint line format -->" + line + "<--.");
        }
        String[] endpoints = sLine[1].split(OTHER_SEPARATOR, -1);
        List<String> epList = Arrays.stream(endpoints).filter(s -> !s.trim().isEmpty()).map(String::trim)
                                    .collect(Collectors.toList());
        if (epList.size() > 0) {
            PathwayInterface p = repository.getPathwayById(sLine[0].trim());
            if (p != null) {
                p.getGraph().setEndpoints(epList);
            }
        }
    }

    @Override
    protected RepositoryInterface realReader() {
        RepositoryInterface rep = pf.getRepository();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(getInputStream()))) {
            String line;
            boolean isPathway = false, isNode = false, isEdge = false, isEndpoints = false;
            while ((line = r.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    if (line.equalsIgnoreCase(PATHWAYS_HEADER)) {
                        isPathway = true;
                        isEndpoints = isNode = isEdge = false;
                    } else if (line.equalsIgnoreCase(NODES_HEADER)) {
                        isNode = true;
                        isEndpoints = isPathway = isEdge = false;
                    } else if (line.equalsIgnoreCase(EDGES_HEADER)) {
                        isEdge = true;
                        isEndpoints = isPathway = isNode = false;
                    } else if (line.equalsIgnoreCase(ENDPOINTS_HEADER)) {
                        isEndpoints = true;
                        isEdge = isPathway = isNode = false;
                    } else {
                        if (isPathway) {
                            PathwayInterface p = readPathway(line);
                            rep.add(p);
                        } else if (isNode) {
                            NodeInterface n = readNode(line);
                            allNodes.put(n.getId(), n);
                        } else if (isEdge) {
                            EdgeInterface e = readEdge(line, rep);
                            if (e != null) {
                                e.getDescription().getOwner().getGraph().addEdge(e);
                            }
                        } else if (isEndpoints) {
                            readEndpoints(line, rep);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return rep;
    }
}
