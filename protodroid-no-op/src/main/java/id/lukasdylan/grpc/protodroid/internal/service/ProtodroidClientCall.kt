package id.lukasdylan.grpc.protodroid.internal.service

import io.grpc.*

/**
 * Created by Lukas Dylan on 05/08/20.
 */
internal class ProtodroidClientCall<RequestObject, ResponseObject>(
    method: MethodDescriptor<RequestObject, ResponseObject>,
    callOptions: CallOptions,
    channel: Channel,
) : ForwardingClientCall.SimpleForwardingClientCall<RequestObject, ResponseObject>(
    channel.newCall(method, callOptions)
)