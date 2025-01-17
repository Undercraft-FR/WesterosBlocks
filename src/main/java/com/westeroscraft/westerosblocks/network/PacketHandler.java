package com.westeroscraft.westerosblocks.network;

import java.io.IOException;

import com.westeroscraft.westerosblocks.WesterosBlocks;

import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class PacketHandler extends SimpleChannelInboundHandler<WBPacket>  {
    private void onBlockMsg(INetHandler handler, EntityPlayer player, BlockMsgPacket packet) throws IOException {
        Block blk = Block.getBlockById(packet.blockID);
        if ((blk != null) && (blk instanceof WesterosBlocksMessageDest)) {
            System.out.println("deliverMessage(" + packet.blockID + ")");
            ((WesterosBlocksMessageDest)blk).deliverMessage(handler, player, packet.msgdata);
        }
    }

    @Override
    protected  void channelRead0(ChannelHandlerContext ctx, WBPacket packet) {
        try {
            INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
            EntityPlayer player = WesterosBlocks.proxy.getPlayerFromNetHandler(netHandler);

            if (packet instanceof BlockMsgPacket) {
                onBlockMsg(netHandler, player, (BlockMsgPacket) packet);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}