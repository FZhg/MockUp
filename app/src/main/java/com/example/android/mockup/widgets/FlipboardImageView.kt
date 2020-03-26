package com.example.android.mockup.widgets


import android.animation.AnimatorSet
import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.util.FloatProperty
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView


class FlipboardImageView : androidx.appcompat.widget.AppCompatImageView {
    /**
     * Rotation degree around X or Y axe for the camera.
     * For the X or Y axes, positive degree will rotate the image further away from the screen; Vice versa.
     */
    private var cameraRotation = 0f

    /**
     * This camera generates relevant canvas transformations matrix.
     * Move the camera away from the canvas otherwise the flipping image will became too larger if the image flips close to the screen.
     */
    private val camera = Camera().apply {
        setLocation(0f, 0f, -100f)
    }

    /**
     * Property Wrapper for the above camera rotation degree around y or x axes.
     * This wrapper can be used to avoid heavy payload, which will be caused by reflection if you pass the property name string to ObjectAnimator factory methods.
     * Usage: val animator = ObjectAnimator.ofFloat(view: View, CAMERA_ROTATION, endValue: Float)
     */
    val CAMERA_ROTATION = object : FloatProperty<FlipboardImageView>("cameraRotation") {
        override fun setValue(`object`: FlipboardImageView?, value: Float) {
            `object`!!.cameraRotation = value
            `object`!!.invalidate()
        }

        override fun get(`object`: FlipboardImageView?): Float {
            return `object`!!.cameraRotation
        }

    };


    /**
     * Rotation degree around X or Y axe for the camera.
     * For the X or Y axes, positive degree will rotate the image further away from the screen; Vice versa.
     */
    private var cameraRotationPhaseThree = 0f



    /**
     * Canvas rotation angle around its center at current position;
     * When this angle is positive the canvas rotate clockwise.
     */
    private var canvasRotation = 0f

    /**
     * Property Wrapper for the above Canvas rotation angle round the canvas center
     * This wrapper can be used to avoid heavy payload, which will be caused by reflection if you pass the property name string to ObjectAnimator factory methods.
     * Usage: val animator = ObjectAnimator.ofFloat(view: View, CANVAS_ROTATION, endValue: Float)
     */
    val CANVAS_ROTATION = object : FloatProperty<FlipboardImageView>("canvasRotation") {
        override fun setValue(`object`: FlipboardImageView?, value: Float) {
            `object`!!.canvasRotation = value
            // We don't add invalidate() here since the canvas rotates only when the camera rotates;
            // This is a further step to reduce the times of redrawing.
        }

        override fun get(`object`: FlipboardImageView?): Float {
            return `object`!!.canvasRotation
        }

    };

    /**
     * The animation is divided into following 3 parts which can be described by above two parameters cameraRotation angle and canvasRotation angle.
     */

    /**
     * Phase One Animator.
     * In this phase, camera rotate around Y axes form 0 degree to -45 degree while the canvas stays still
     */
    private val animatorPhaseOne = createAnimatorPhaseOne(1000L)

    /**
     * Phase Two
     * Step 1:
     * camera rotate around Y or X axes from -45 degree to zero and then around X or Y axes from 0 degree to -45 degree,
     * while the canvas will rotate from 0 to 45 degree and finally to 90 degree in 2 different quadrants
     * Step 2:
     * Then repeat Step 1 for 3 times.
     */
    private val animatorPhaseTwo = createAnimatorPhaseTwo(2400L)

    /**
     * Phase Three
     * The former still part rotates to be perpendicular to other half current rotated image
     */

    /**
     * Property Wrapper for the above camera rotation degree around y or x axes.
     * This wrapper can be used to avoid heavy payload, which will be caused by reflection if you pass the property name string to ObjectAnimator factory methods.
     * Usage: val animator = ObjectAnimator.ofFloat(view: View, CAMERA_ROTATION, endValue: Float)
     */
    val CAMERA_ROTATION_PHASE_THREE = object : FloatProperty<FlipboardImageView>("cameraRotationPhaseThree") {
        override fun setValue(`object`: FlipboardImageView?, value: Float) {
            `object`!!.cameraRotationPhaseThree = value
            `object`!!.invalidate()
        }

        override fun get(`object`: FlipboardImageView?): Float {
            return `object`!!.cameraRotationPhaseThree
        }

    };
    private val animatorPhaseThree = createAnimatorPhaseThree(1000L)


    /**
     * The choreographer for the animations
     */
    private val animatorSet = AnimatorSet().apply {
        playSequentially(animatorPhaseOne, animatorPhaseTwo, animatorPhaseThree)
//        play(animatorPhaseOne)
    }

    /**
     * This bound clip the still parts of the image.
     */
    private val bound = RectF()

    /**
     * These public constructors  inflate the view by code or xml files
     */
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    /**
     * Create the animator for Phase One
     */
    private fun createAnimatorPhaseOne(phaseOneDuration: Long): ObjectAnimator {
        val cameraRotationPhaseOneStartingKeyframe: Keyframe = Keyframe.ofFloat(0f, 0f)
        val cameraRotationPhaseOneEndKeyframe: Keyframe =
            Keyframe.ofFloat(1f, -50f).apply { interpolator = LinearInterpolator() }
        val cameraRotationPhaseOneHolder = PropertyValuesHolder.ofKeyframe(
            CAMERA_ROTATION,
            cameraRotationPhaseOneStartingKeyframe,
            cameraRotationPhaseOneEndKeyframe
        )
        return ObjectAnimator.ofPropertyValuesHolder(this, cameraRotationPhaseOneHolder)
            .apply { duration = phaseOneDuration }
    }

    /**
     * Create the animator for Phase Two
     * For this Phase, the animator will be AnimatorSet for 2 similar steps
     */
    private fun createAnimatorPhaseTwo(phaseTwoDuration: Long): ObjectAnimator {
        val cameraRotationPhaseTwoKeyframe1: Keyframe = Keyframe.ofFloat(0f, -50f)
        val cameraRotationPhaseTwoKeyframe2: Keyframe = Keyframe.ofFloat(0.3f, 0f)
        val cameraRotationPhaseTwoKeyframe3: Keyframe = Keyframe.ofFloat(1f, -50f)


        val cameraRotationPhaseTwoHolder = PropertyValuesHolder.ofKeyframe(
            CAMERA_ROTATION,
            cameraRotationPhaseTwoKeyframe1,
            cameraRotationPhaseTwoKeyframe2,
            cameraRotationPhaseTwoKeyframe3
        )
        val canvasRotationPhaseTwoStartingKeyframe: Keyframe = Keyframe.ofFloat(0f, 0f);
        val canvasRotationPhaseTwoEndKeyframe: Keyframe = Keyframe.ofFloat(1f, -270f);
        val canvasRotationPhaseTwoHolder = PropertyValuesHolder.ofKeyframe(
            CANVAS_ROTATION,
            canvasRotationPhaseTwoStartingKeyframe,
            canvasRotationPhaseTwoEndKeyframe
        );
        return ObjectAnimator.ofPropertyValuesHolder(
            this,
            cameraRotationPhaseTwoHolder,
            canvasRotationPhaseTwoHolder
        ).apply { duration = phaseTwoDuration }
    }

    /**
     * Create the animator for Phase Three
     */
    private fun createAnimatorPhaseThree(phaseThreeDuration: Long): ObjectAnimator {
        val cameraRotationPhaseThreeStartingKeyframe: Keyframe = Keyframe.ofFloat(0f, 0f)
        val cameraRotationPhaseThreeEndKeyframe: Keyframe =
            Keyframe.ofFloat(1f, 40f)
        val cameraRotationPhaseThreeHolder = PropertyValuesHolder.ofKeyframe(
            CAMERA_ROTATION_PHASE_THREE,
            cameraRotationPhaseThreeStartingKeyframe,
            cameraRotationPhaseThreeEndKeyframe
        )
        return ObjectAnimator.ofPropertyValuesHolder(this, cameraRotationPhaseThreeHolder)
            .apply { duration = phaseThreeDuration }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animatorSet.start()
    }


    override fun onDraw(canvas: Canvas) {
        // Transformation to the canvas should be applied in a reverse order since in the formula of AB mMatrix = A(B mMatrix) B is actually the preconcated one.
        val (centerX, centerY) = this.getCenterCoordinates()

        canvas.save()
        canvas.translate(centerX, centerY)
        canvas.rotate(canvasRotation)
        camera.save()
        camera.rotateY(cameraRotation)
        camera.applyToCanvas(canvas)
        camera.restore()
        //Right Now, the image is at (0, 0)
        bound.set(0f, -centerY, centerX, centerY)
        canvas.clipRect(bound)
        canvas.rotate(-canvasRotation)
        canvas.translate(-centerX, -centerY)
        super.onDraw(canvas)
        canvas.restore()

        canvas.save()
        canvas.translate(centerX, centerY)
        canvas.rotate(canvasRotation)
        //Right Now, the image is at (0, 0)
        camera.save()
        camera.rotateY(cameraRotationPhaseThree)
        camera.applyToCanvas(canvas)
        camera.restore()
        bound.set(-centerX, -centerY, 0f, centerY)
        canvas.clipRect(bound)
        canvas.rotate(-canvasRotation)
        canvas.translate(-centerX, -centerY)
        super.onDraw(canvas)

//         Actually, I'm worried that the canvasRotation accessed here will be different from above due to different timing.
//         I'm aware that the UI thread is not thread-safe, which means everything created in this thread stays here forever.
//         But could objectAnimator update the value between the drawing of part1 and part 2.
//         In this case, the animation will be gnarly.
        canvas.restore()
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animatorSet.end()
    }
}

/**
 * A helper function returns the center point coordinates of imageView relative to its parent.
 */
fun ImageView.getCenterCoordinates(): Pair<Float, Float> {
    val centerX = width / 2f
    val centerY = height / 2f
    return Pair(centerX, centerY)
}