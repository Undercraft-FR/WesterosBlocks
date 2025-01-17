package com.westeroscraft.westerosblocks.render;

import com.westeroscraft.westerosblocks.WesterosBlocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import org.lwjgl.opengl.GL11;

// Custom fence renderer - lets us render fence items with per-meta specific textures in inventory
public class WCLadderRenderer implements ISimpleBlockRenderingHandler {
    @Override
    public void renderInventoryBlock(Block block, int meta, int modelID,
            RenderBlocks renderer) {
        
        Tessellator tessellator = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
            Block block, int modelId, RenderBlocks renderer) {
        int meta = world.getBlockMetadata(x, y, z);

        switch (meta >> 2) {
            case 0: // Was 5
                renderer.setRenderBounds(0.05, 0.0, 0.0, 0.05, 1.0, 1.0);
                break;
            case 1: // Was 4
                renderer.setRenderBounds(0.95, 0.0, 0.0, 0.95, 1.0, 1.0);
                break;
            case 2: // Was 3
                renderer.setRenderBounds(0.0, 0.0, 0.05, 1.0, 1.0, 0.05);
                break;
            case 3: // Was 2
                renderer.setRenderBounds(0.0, 0.0, 0.95, 1.0, 1.0, 0.95);
                break;
        }
        renderer.renderStandardBlock(block, x, y, z);
        renderer.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);

        return true;
    }

    @Override
    public int getRenderId() {
        return WesterosBlocks.ladderRenderID;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
     }

}
