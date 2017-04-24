package cz.incad.kramerius.rest.api.k5.client.item;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;

/**
 * Created by rumanekm on 11/19/15.
 */
@Path("/v5.0/iiif")
public class IiifResource {
    
    /* DISABLED ON CDK because of java 1.7
    public static final Logger LOGGER = Logger.getLogger(IiifResource.class.getName());

    @Inject
    private SolrMemoization solrMemoization;

    @Inject
    @Named("securedFedoraAccess")
    private FedoraAccess fedoraAccess;

    @Inject
    private SolrAccess solrAccess;

    @Inject
    private Provider<HttpServletRequest> requestProvider;

    @GET
    @Path("{pid}/manifest")
    @Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8" })
    public Response manifest(@PathParam("pid") String pid) {
        String json = "";
        if (pid != null) {
            checkPid(pid);

            try {
                String title = null;
                Element indexDoc = this.solrMemoization.getRememberedIndexedDoc(pid);
                if (indexDoc == null) {
                    indexDoc = this.solrMemoization.askForIndexDocument(pid);
                }
                if (indexDoc != null) {
                    title = SOLRUtils.value(indexDoc, "root_title", String.class);
                }

                PropertyValue titleLabel = new PropertyValueSimpleImpl(title);
                Manifest manifest = new ManifestImpl(new URI(
                        ApplicationURL.applicationURL(this.requestProvider.get()) + basicURL(pid) + "/manifest"),
                        titleLabel);
                List<String> children = null;
                List<String> fieldList = new ArrayList<String>();
                List<Canvas> canvases = new ArrayList<Canvas>();
                children = ItemResourceUtils.solrChildrenPids(pid, fieldList, solrAccess, solrMemoization);

                for (String p : children) {
                    String repPid = p.replace("/", "");
                    if (repPid.equals(pid)) {
                        continue;
                    }

                    String titlePage = null;
                    Element indexDocChild = this.solrMemoization.getRememberedIndexedDoc(repPid);
                    if (indexDocChild == null) {
                        indexDocChild = this.solrMemoization.askForIndexDocument(repPid);
                    }
                    if (indexDocChild != null) {
                        String model = SOLRUtils.value(indexDocChild, "fedora.model", String.class);
                        if (!"page".equals(model))
                            continue;

                        titlePage = SOLRUtils.value(indexDocChild, "dc.title", String.class);
                    }

                    String id = ApplicationURL.applicationURL(this.requestProvider.get()) + "/canvas/" + repPid;
                    Pair<Integer, Integer> resolution = getResolution(repPid);
                    if (resolution != null) {
                        Canvas canvas = new CanvasImpl(id, new PropertyValueSimpleImpl(titlePage), resolution.getLeft(),
                                resolution.getRight());

                        ImageResource resource = new ImageResourceImpl();
                        String resourceId = ApplicationURL.applicationURL(this.requestProvider.get()).toString() + "/iiif/"
                                + repPid + "/full/full/0/default.jpg";
                        resource.setType("dctypes:Image");
                        resource.setId(resourceId);
                        resource.setHeight(resolution.getLeft());
                        resource.setWidth(resolution.getRight());
                        resource.setFormat("image/jpeg");

                        Service service = new ServiceImpl();
                        service.setContext("http://iiif.io/api/image/2/context.json");
                        service.setId(
                                ApplicationURL.applicationURL(this.requestProvider.get()).toString() + "/iiif/" + repPid);
                        service.setProfile("http://iiif.io/api/image/2/level1.json");

                        resource.setService(service);
                        Image image = new ImageImpl();
                        image.setOn(new URI(id));`
                        image.setResource(resource);
                        canvas.setImages(Collections.singletonList(image));
                        canvases.add(canvas);
                    }
                }

                // no pages - 500 ? 
                if (canvases.isEmpty()) {
                    throw new GenericApplicationException("cannot create manifest for pid '"+pid+"'");
                }
                    
                Sequence sequence = new SequenceImpl();
                sequence.setCanvases(canvases);
                manifest.setSequences(Collections.singletonList(sequence));

                return Response.ok().entity(toJSON(manifest)).build();
            } catch (IOException e) {
                throw new GenericApplicationException(e.getMessage());
            } catch (URISyntaxException e) {
                throw new GenericApplicationException(e.getMessage());
            } catch (XPathExpressionException e) {
                throw new GenericApplicationException(e.getMessage());
            }
        } else {
            throw new BadRequestException("expecting pid parameter");
        }

    }

    private String toJSON(Manifest manifest) throws JsonProcessingException {
        IiifPresentationApiObjectMapper objectMapper = new IiifPresentationApiObjectMapper();
        String jsonString = objectMapper.writeValueAsString(manifest);
        return jsonString;
    }

    private Pair<Integer, Integer> getResolution(String pid) throws XPathExpressionException, IOException {
        String iiifEndpoint = null;
        iiifEndpoint = IIIFUtils.iiifImageEndpoint(pid, this.fedoraAccess);
        return iiifEndpoint != null ? resolution(iiifEndpoint) : null;
    }

    private Pair<Integer, Integer> resolution(String url) throws MalformedURLException, IOException {
        URLConnection con = RESTHelper.openConnection(url + "/info.json", "", "");
        InputStream inputStream = null;
        inputStream = con.getInputStream();
        String json = org.apache.commons.io.IOUtils.toString(inputStream, Charset.defaultCharset());
        final org.json.JSONObject jsonObject = new org.json.JSONObject(json);

        final Integer width = jsonObject.getInt("width");
        final Integer height = jsonObject.getInt("height");

        return new Pair<Integer, Integer>() {
            @Override
            public Integer getLeft() {
                return height;
            }

            @Override
            public Integer getRight() {
                return width;
            }

            @Override
            public Integer setValue(Integer value) {
                return null;
            }
        };
    }

    protected void checkPid(String pid) throws PIDNotFound {
        try {
            if (PIDSupport.isComposedPID(pid)) {
                String p = PIDSupport.first(pid);
                this.fedoraAccess.getRelsExt(p);
            } else {
                this.fedoraAccess.getRelsExt(pid);
            }
        } catch (IOException e) {
            throw new PIDNotFound("pid not found");
        } catch (Exception e) {
            throw new PIDNotFound("error while parsing pid (" + pid + ")");
        }
    }

   */
    /**
     * Basic URL
     *
     * @param pid
     * @return
     */
    public static String basicURL(String pid) {
        String uriString = UriBuilder.fromResource(ItemResource.class).path("{pid}").build(pid).toString();
        return uriString;
    }

}
