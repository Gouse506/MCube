package vmc.mcube.in.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import vmc.mcube.in.activity.HomeActivity;

/**
 * Created by mukesh on 8/7/15.
 */
public class AnimationUtils {
  /*  public static void animate(RecyclerView.ViewHolder holder, Boolean Down) {

        if(Constants.Anim) {

            *//*if (!Down) {
                YoYo.with(Techniques.Pulse)
                        .duration(1000)
                        .playOn(holder.itemView);
            } else {
                *//**//*YoYo.with(Techniques.Pulse)
                        .duration(500)
                        .playOn(holder.itemView);*//**//*
            }*//*
        }

    }*/
    public static void animateView(View view) {


            YoYo.with(Techniques.RubberBand)
                    .duration(1000)
                    .playOn(view);


    }
}
