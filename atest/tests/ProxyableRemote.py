'''d'''
from xmlrpc import client
from http import client as httpclient
from robot.libraries.Remote import Remote, XmlRpcRemoteClient


class ProxyableRemote(Remote):
    """A Remote library proxy that can optionally operate through a proxy, such as Fiddler, for debugging XML-RPC traffic. Example value for proxy: localhost:8888
    """

    def __init__(self, uri, proxy=None):
        Remote.__init__(self, uri)
        self._client = ProxyableXmlRpcRemoteClient(uri, proxy)


class ProxyableXmlRpcRemoteClient(XmlRpcRemoteClient):

    def __init__(self, uri, proxy=None):
        tp = None
        if proxy is not None:
            tp = ProxiedTransport()
            tp.set_proxy(proxy)
        self._server = client.ServerProxy(uri, transport=tp, encoding='UTF-8')


class ProxiedTransport(server.Transport):

    def set_proxy(self, proxy):
        self.proxy = proxy

    def make_connection(self, host):
        self.realhost = host
        if self.proxy is None:
            h = httpclient.HTTPConnection()
        else:
            h = httpclient.HTTPConnection(self.proxy)
        self._connection = host, h
        return h

    def send_request(self, connection, handler, request_body):
        connection.putrequest("POST", 'http://%s%s' % (self.realhost, handler))

    def send_host(self, connection, host):
        connection.putheader('Host', self.realhost)
