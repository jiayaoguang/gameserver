// Generated by the protocol buffer compiler.  DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: MsgChat.proto
// Protobuf Java Version: 4.28.0

package org.jyg.gameserver.test.proto;

public final class MsgChatOuterClass {
  private MsgChatOuterClass() {}
  static {
    com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
      com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
      /* major= */ 4,
      /* minor= */ 28,
      /* patch= */ 0,
      /* suffix= */ "",
      MsgChatOuterClass.class.getName());
  }
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_jyg_gameserver_core_proto_MsgChat_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_org_jyg_gameserver_core_proto_MsgChat_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rMsgChat.proto\022\035org.jyg.gameserver.core" +
      ".proto\"&\n\007MsgChat\022\n\n\002id\030\001 \001(\005\022\017\n\007content" +
      "\030\002 \001(\tB!\n\035org.jyg.gameserver.test.protoP" +
      "\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_org_jyg_gameserver_core_proto_MsgChat_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_org_jyg_gameserver_core_proto_MsgChat_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_org_jyg_gameserver_core_proto_MsgChat_descriptor,
        new java.lang.String[] { "Id", "Content", });
    descriptor.resolveAllFeaturesImmutable();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
