package javax.servlet;

import javax.servlet.annotation.ServletSecurity;
import java.util.ResourceBundle;

/**
 * Equivalent of HttpConstraint for
 * programmatic configuration of security constraints
 *
 * Created by xjk on 3/4/17.
 */
public class HttpConstraintElement {

    private static final String LSTRING_FILE = "javax.servlet.LocalStrings";
    private static final ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);

    private final ServletSecurity.EmptyRoleSemantic emptyRoleSemantic;
    private final ServletSecurity.TransportGuarantee transportGuarantee;
    private final String[] rolesAllowed;



    /**
     * Default constraint is permit with no transport guarantee.
     */
    public HttpConstraintElement() {
        // Default constructor
        this.emptyRoleSemantic = ServletSecurity.EmptyRoleSemantic.PERMIT;
        this.transportGuarantee = ServletSecurity.TransportGuarantee.NONE;
        this.rolesAllowed = new String[0];
    }

    /**
     * Construct a constraint with an empty role semantic. Typically used with
     * {@link ServletSecurity.EmptyRoleSemantic#DENY}.
     *
     * @param emptyRoleSemantic The empty role semantic to apply to the newly
     *                          created constraint
     */
    public HttpConstraintElement(ServletSecurity.EmptyRoleSemantic emptyRoleSemantic) {
        this.emptyRoleSemantic = emptyRoleSemantic;
        this.transportGuarantee = ServletSecurity.TransportGuarantee.NONE;
        this.rolesAllowed = new String[0];
    }

    /**
     * Construct a constraint with a transport guarantee and roles.
     *
     * @param transportGuarantee The transport guarantee to apply to the newly
     *                           created constraint
     * @param rolesAllowed       The roles to associate with the newly created
     *                           constraint
     */
    public HttpConstraintElement(ServletSecurity.TransportGuarantee transportGuarantee,
                                 String... rolesAllowed) {
        this.emptyRoleSemantic = ServletSecurity.EmptyRoleSemantic.PERMIT;
        this.transportGuarantee = transportGuarantee;
        this.rolesAllowed = rolesAllowed;
    }

    /**
     * Construct a constraint with an empty role semantic, a transport guarantee
     * and roles.
     *
     * @param emptyRoleSemantic The empty role semantic to apply to the newly
     *                          created constraint
     * @param transportGuarantee The transport guarantee to apply to the newly
     *                           created constraint
     * @param rolesAllowed       The roles to associate with the newly created
     *                           constraint
     * @throws IllegalArgumentException if roles are specified when DENY is used
     */
    public HttpConstraintElement(ServletSecurity.EmptyRoleSemantic emptyRoleSemantic,
                                 ServletSecurity.TransportGuarantee transportGuarantee, String... rolesAllowed) {
        if (rolesAllowed != null && rolesAllowed.length > 0 &&
                ServletSecurity.EmptyRoleSemantic.DENY.equals(emptyRoleSemantic)) {
            throw new IllegalArgumentException(lStrings.getString(
                    "httpConstraintElement.invalidRolesDeny"));
        }
        this.emptyRoleSemantic = emptyRoleSemantic;
        this.transportGuarantee = transportGuarantee;
        this.rolesAllowed = rolesAllowed;
    }

    /**
     * TODO
     * @return TODO
     */
    public ServletSecurity.EmptyRoleSemantic getEmptyRoleSemantic() {
        return emptyRoleSemantic;
    }

    /**
     * TODO
     * @return TODO
     */
    public ServletSecurity.TransportGuarantee getTransportGuarantee() {
        return transportGuarantee;
    }

    /**
     * TODO
     * @return TODO
     */
    public String[] getRolesAllowed() {
        return rolesAllowed;
    }
}
