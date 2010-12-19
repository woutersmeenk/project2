package project2.triggers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DebugResponse implements Response {
    private static final Log LOG = LogFactory.getLog(DebugResponse.class);
    private final String text;

    public DebugResponse(final String text) {
        super();
        this.text = text;
    }

    @Override
    public void execute() {
        LOG.info(text);
    }

}
