package com.github.zlbovolini

import com.github.zlbovolini.hello.ErrorDetails
import com.github.zlbovolini.hello.HelloGrpcReply
import com.github.zlbovolini.hello.HelloGrpcRequest
import com.github.zlbovolini.hello.HelloGrpcServiceGrpc
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.exceptions.HttpStatusException

interface HelloResponse {

}

@Controller("/api/hello")
class HelloController(val grpcClient: HelloGrpcServiceGrpc.HelloGrpcServiceBlockingStub) {

    @Get
    fun hello(@QueryValue name: String): HttpResponse<Any> {

        val request = HelloGrpcRequest.newBuilder()
            .setName(name)
            .build()


        val response: HelloGrpcReply = try {
            grpcClient.send(request)
        } catch (e: StatusRuntimeException) {
            //throw HttpStatusException(HttpStatus.BAD_REQUEST, e.status.description)

            val statusProto = StatusProto.fromThrowable(e)

            if (statusProto == null) {
                throw HttpStatusException(HttpStatus.BAD_REQUEST, e.status.description)
            }

            val anyDetails = statusProto.detailsList[0]

            val details = anyDetails.unpack(ErrorDetails::class.java)

            throw HttpStatusException(HttpStatus.FORBIDDEN, "${details.code}: ${details.message}")
        }
        val httpResponse = object : HelloResponse {
            val message = response.message
        }

        return HttpResponse.ok(httpResponse)
    }
}