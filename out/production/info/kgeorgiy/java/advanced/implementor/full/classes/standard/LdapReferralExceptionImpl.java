package info.kgeorgiy.java.advanced.implementor.full.classes.standard;

public class LdapReferralExceptionImpl extends info.kgeorgiy.java.advanced.implementor.full.classes.standard.LdapReferralException {
    protected  LdapReferralExceptionImpl() { 
        super();
    }

    protected  LdapReferralExceptionImpl(java.lang.String arg0) { 
        super(arg0);
    }

    public javax.naming.Context getReferralContext(java.util.Hashtable arg0, javax.naming.ldap.Control[] arg1) throws javax.naming.NamingException { 
        return null;
    }

    public boolean skipReferral() { 
        return false;
    }

    public void retryReferral() { 
        return;
    }

    public javax.naming.Context getReferralContext() throws javax.naming.NamingException { 
        return null;
    }

    public java.lang.Object getReferralInfo() { 
        return null;
    }

    public javax.naming.Context getReferralContext(java.util.Hashtable arg0) throws javax.naming.NamingException { 
        return null;
    }

}
