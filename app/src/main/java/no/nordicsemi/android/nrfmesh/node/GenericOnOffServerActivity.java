package no.nordicsemi.android.nrfmesh.node;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.models.GenericOnOffServerModel;
import no.nordicsemi.android.mesh.models.NodeStatus;
import no.nordicsemi.android.mesh.opcodes.ApplicationMessageOpCodes;
import no.nordicsemi.android.mesh.transport.Element;
import no.nordicsemi.android.mesh.transport.GenericOnOffGet;
import no.nordicsemi.android.mesh.transport.GenericOnOffSet;
import no.nordicsemi.android.mesh.transport.GenericOnOffStatus;
import no.nordicsemi.android.mesh.transport.MeshMessage;
import no.nordicsemi.android.mesh.transport.MeshModel;
import no.nordicsemi.android.mesh.transport.ProvisionedMeshNode;
import no.nordicsemi.android.mesh.utils.MeshAddress;
import no.nordicsemi.android.mesh.utils.MeshParserUtils;
import no.nordicsemi.android.nrfmesh.R;
import no.nordicsemi.android.nrfmesh.service.NotifyFactory;

public class GenericOnOffServerActivity extends BaseModelConfigurationActivity {

    private static final String TAG = GenericOnOffServerActivity.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    private TextView onOffState;
    private TextView remainingTime;
    private Button mActionOnOff;
    protected int mTransitionStepResolution;
    protected int mTransitionSteps;
    NotifyFactory factoryNotify;
    List<Integer> elementss = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final MeshModel model = mViewModel.getSelectedModel().getValue();
        if (model instanceof GenericOnOffServerModel) {
            final ConstraintLayout container = findViewById(R.id.node_controls_container);
            final View nodeControlsContainer = LayoutInflater.from(this).inflate(R.layout.layout_generic_on_off, container);
            final TextView time = nodeControlsContainer.findViewById(R.id.transition_time);
            onOffState = nodeControlsContainer.findViewById(R.id.on_off_state);
            remainingTime = nodeControlsContainer.findViewById(R.id.transition_state);
            final SeekBar transitionTimeSeekBar = nodeControlsContainer.findViewById(R.id.transition_seekbar);
            transitionTimeSeekBar.setProgress(0);
            transitionTimeSeekBar.incrementProgressBy(1);
            transitionTimeSeekBar.setMax(230);


            factoryNotify = new NotifyFactory(getApplication(), 0);
            factoryNotify.notifyProgressNotification(true, 0);
            final SeekBar delaySeekBar = nodeControlsContainer.findViewById(R.id.delay_seekbar);
            delaySeekBar.setProgress(0);
            delaySeekBar.setMax(255);
            final TextView delayTime = nodeControlsContainer.findViewById(R.id.delay_time);
            Button send, receid, assign, changegame, startgame, stopgame;

            assign = nodeControlsContainer.findViewById(R.id.assignAdress);
            changegame = nodeControlsContainer.findViewById(R.id.changeGame);
            startgame = nodeControlsContainer.findViewById(R.id.staartgame);
            stopgame = nodeControlsContainer.findViewById(R.id.stopgame);

            assign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendGenericASSign(ApplicationMessageOpCodes.GENERIC_ON_OFF_SET);
                }
            });

            changegame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendGenericOnOff(GenericOnOffSet.LOCAL_CHANGE, ApplicationMessageOpCodes.GENERIC_ON_OFF_SET);
                }
            });


            stopgame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendGenericOnOff(GenericOnOffSet.LOCAL_STOP, ApplicationMessageOpCodes.GENERIC_ON_OFF_SET);
                }
            });


            startgame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int n : elementss) {
                        byte[] bb = getColorRandom();
                        sendColorTo(getElem2Node(n)
                                , GenericOnOffSet.LOCAL_START, optcode, bb);
                    }
                    return;

                }
            });


            mActionOnOff = nodeControlsContainer.findViewById(R.id.action_on);
            mActionOnOff.setOnClickListener(v -> {
                try {
                    if (mActionOnOff.getText().toString().equals(getString(R.string.action_generic_on))) {
                        sendGenericOnOff(true, delaySeekBar.getProgress());
                    } else {
                        sendGenericOnOff(false, delaySeekBar.getProgress());
                    }
                } catch (IllegalArgumentException ex) {
                    mViewModel.displaySnackBar(this, mContainer, ex.getMessage(), Snackbar.LENGTH_LONG);
                }
            });

            mActionRead = nodeControlsContainer.findViewById(R.id.action_read);
            mActionRead.setOnClickListener(v -> sendGenericOnOffGet());

            transitionTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int lastValue = 0;
                double res = 0.0;

                @Override
                public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {

                    if (progress >= 0 && progress <= 62) {
                        lastValue = progress;
                        mTransitionStepResolution = 0;
                        mTransitionSteps = progress;
                        res = progress / 10.0;
                        time.setText(getString(R.string.transition_time_interval, String.valueOf(res), "s"));
                    } else if (progress >= 63 && progress <= 118) {
                        if (progress > lastValue) {
                            mTransitionSteps = progress - 56;
                            lastValue = progress;
                        } else if (progress < lastValue) {
                            mTransitionSteps = -(56 - progress);
                        }
                        mTransitionStepResolution = 1;
                        time.setText(getString(R.string.transition_time_interval, String.valueOf(mTransitionSteps), "s"));

                    } else if (progress >= 119 && progress <= 174) {
                        if (progress > lastValue) {
                            mTransitionSteps = progress - 112;
                            lastValue = progress;
                        } else if (progress < lastValue) {
                            mTransitionSteps = -(112 - progress);
                        }
                        mTransitionStepResolution = 2;
                        time.setText(getString(R.string.transition_time_interval, String.valueOf(mTransitionSteps * 10), "s"));
                    } else if (progress >= 175 && progress <= 230) {
                        if (progress >= lastValue) {
                            mTransitionSteps = progress - 168;
                            lastValue = progress;
                        } else {
                            mTransitionSteps = -(168 - progress);
                        }
                        mTransitionStepResolution = 3;
                        time.setText(getString(R.string.transition_time_interval, String.valueOf(mTransitionSteps * 10), "min"));
                    }
                }

                @Override
                public void onStartTrackingTouch(final SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(final SeekBar seekBar) {

                }
            });

            delaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
                    delayTime.setText(getString(R.string.transition_time_interval, String.valueOf(progress * MeshParserUtils.GENERIC_ON_OFF_5_MS), "ms"));
                }

                @Override
                public void onStartTrackingTouch(final SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(final SeekBar seekBar) {

                }
            });
        }

        NODES = mViewModel.getNodes().getValue();
        nodecount = NODES.size();
        for (ProvisionedMeshNode node : NODES) {
            Pair<Element, MeshModel> elModel0 = getElementModel(node);
            elementss.add(elModel0.first.getElementAddress());
            final NodeStatus status = new NodeStatus(elModel0.first.getElementAddress(), false, false, -1);
            GenericOnOffStatus.statusMesh.put(status.getId(), status);
        }


    }

    int nodecount = 0;

    public static List<ProvisionedMeshNode> NODES;
    public static byte cmand = 0;

    public void sendGenericASSign(int optcode) {
        if (!checkConnectivity()) return;
        pam.clear();
        Thread tresd = new Thread(new Runnable() {
            @Override
            public void run() {
                int address2set = 2;
                Boolean isnode = false;
                int count = NODES.size();

                for (ProvisionedMeshNode node : NODES) {
                    Pair<Element, MeshModel> elModel = getElementModel(node);
                    Element element = elModel.first;
                    MeshModel model = elModel.second;

                    sendTo(element, node, model, GenericOnOffSet.LOCAL_ASSIGN, optcode, count, address2set);
                    address2set++;
                }
            }
        });
        tresd.start();
    }

    public void sendGenericOnOff(byte cmd, int optcode) {
        if (!checkConnectivity()) return;
        pam.clear();
        cmand = cmd;
        Thread tresd = new Thread(new Runnable() {
            @Override
            public void run() {
                int address2set = 2;
                Boolean isnode = false;
                int count = NODES.size();

                for (ProvisionedMeshNode node : NODES) {
                    Pair<Element, MeshModel> elModel = getElementModel(node);
                    Element element = elModel.first;
                    MeshModel model = elModel.second;

                    sendTo(element, node, model, cmd, optcode, count, address2set);
                }
            }
        });
        tresd.start();
    }

    public void sendGenericOnOff2Node(ProvisionedMeshNode node, byte cmd, int optcode) {
        if (!checkConnectivity()) return;
        pam.clear();
        cmand = cmd;
        Thread tresd = new Thread(new Runnable() {
            @Override
            public void run() {
                int address2set = 2;
                Boolean isnode = false;
                int count = NODES.size();
                Pair<Element, MeshModel> elModel = getElementModel(node);
                Element element = elModel.first;
                MeshModel model = elModel.second;

                sendTo(element, node, model, cmd, optcode, count, address2set);
            }
        });
        tresd.start();
    }

    ProvisionedMeshNode randomNode() {
        Random random = new Random();
        int node = random.nextInt(2);
        if (node >= NODES.size()) node = 0;
        return NODES.get(node);
    }

    int randomInt(int bound) {
        Random random = new Random();
        return random.nextInt(bound);

    }

    byte[] getColorRandom() {
        byte[] color = new byte[3];
        color[0] = (byte) randomInt(255);
        color[1] = (byte) randomInt(255);
        color[2] = (byte) randomInt(255);
        return color;
    }

    ProvisionedMeshNode getElem2Node(int elementAddress) {
        for (ProvisionedMeshNode node : NODES) {
            Element element = null;
            for (int kel : node.getElements().keySet()) {
                element = node.getElements().get(kel);
                if (element.getElementAddress() == elementAddress)
                    return node;
            }
        }
        return null;
    }

    Pair<Element, MeshModel> getElementModel(ProvisionedMeshNode node) {

        Element element = null;
        MeshModel model = null;
        boolean isnode = false;
        for (int kel : node.getElements().keySet()) {
            element = node.getElements().get(kel);
            if (element != null)
                for (int kmod : element.getMeshModels().keySet()) {
                    model = element.getMeshModels().get(kmod);
                    if (model.getType() == 3) {
                        isnode = true;
                        break;
                    }

                }
            if (isnode)
                break;
        }
        return Pair.create(element, model);
    }

    void sendTo(Element element, ProvisionedMeshNode node, MeshModel model, byte cmd, int optcode, int count, int setAddress) {
        if (element != null) {
            if (model != null) {
                if (!model.getBoundAppKeyIndexes().isEmpty()) {
                    final int appKeyIndex = model.getBoundAppKeyIndexes().get(0);
                    final ApplicationKey appKey = mViewModel.getNetworkLiveData().getMeshNetwork().getAppKey(appKeyIndex);

                    final int address = element.getElementAddress();

                    final GenericOnOffSet genericOnOffSet = new GenericOnOffSet(appKey, setAddress,
                            node.getSequenceNumber(), count, cmd, optcode);
                    if (cmd == GenericOnOffSet.LOCAL_ASSIGN)
                        sendMessage(address, genericOnOffSet);
                    else {
                        sendMessage(address, genericOnOffSet);
                        return;
                    }
                    Log.e("Pre", cmd + "");
                } else {
                    mViewModel.displaySnackBar(GenericOnOffServerActivity.this, mContainer, getString(R.string.error_no_app_keys_bound), Snackbar.LENGTH_LONG);
                }
            }
        }
    }

    void sendTo(ProvisionedMeshNode node, byte cmd, int optcode) {
        Pair<Element, MeshModel> elmodel = getElementModel(node);
        Element element = elmodel.first;
        MeshModel model = elmodel.second;
        if (element != null) {
            if (model != null) {
                if (!model.getBoundAppKeyIndexes().isEmpty()) {
                    final int appKeyIndex = model.getBoundAppKeyIndexes().get(0);
                    final ApplicationKey appKey = mViewModel.getNetworkLiveData()
                            .getMeshNetwork().getAppKey(appKeyIndex);

                    final int address = element.getElementAddress();

                    final GenericOnOffSet genericOnOffSet = new GenericOnOffSet(appKey, 0,
                            node.getSequenceNumber(), 0, cmd, optcode);

                    sendMessage(address, genericOnOffSet);

                    Log.e("Pre", cmd + "");
                } else {
                    mViewModel.displaySnackBar(GenericOnOffServerActivity.this, mContainer, getString(R.string.error_no_app_keys_bound), Snackbar.LENGTH_LONG);
                }
            }
        }
    }

    void sendColorTo(ProvisionedMeshNode node, byte cmd, int optcode, byte[] colors) {
        Pair<Element, MeshModel> elmodel = getElementModel(node);
        Element element = elmodel.first;
        MeshModel model = elmodel.second;
        if (element != null) {
            if (model != null) {
                if (!model.getBoundAppKeyIndexes().isEmpty()) {
                    final int appKeyIndex = model.getBoundAppKeyIndexes().get(0);
                    final ApplicationKey appKey = mViewModel.getNetworkLiveData().getMeshNetwork().getAppKey(appKeyIndex);

                    final int address = element.getElementAddress();
                    final GenericOnOffSet genericOnOffSet = new GenericOnOffSet(appKey,
                            node.getSequenceNumber(), cmd, optcode, colors);

                    sendMessage(address, genericOnOffSet);
                    Log.e("Pre", cmd + "");
                } else {
                    mViewModel.displaySnackBar(GenericOnOffServerActivity.this, mContainer, getString(R.string.error_no_app_keys_bound), Snackbar.LENGTH_LONG);
                }
            }
        }
    }

    @Override
    protected void enableClickableViews() {
        super.enableClickableViews();
        if (mActionOnOff != null && !mActionOnOff.isEnabled())
            mActionOnOff.setEnabled(true);
    }

    @Override
    protected void disableClickableViews() {

        super.disableClickableViews();
        if (mActionOnOff != null)
            mActionOnOff.setEnabled(false);
    }

    int optcode = ApplicationMessageOpCodes.GENERIC_ON_OFF_SET;
    List<byte[]> pam = new ArrayList<>();

    @Override
    protected void updateMeshMessage(final MeshMessage meshMessage) {
        super.updateMeshMessage(meshMessage);
        if (meshMessage instanceof GenericOnOffStatus) {
            GenericOnOffStatus msg = (GenericOnOffStatus) meshMessage;
            Message handmsg = new Message();
            handmsg.obj = msg;
            posHandle.sendMessage(handmsg);
        }


//        if (meshMessage instanceof GenericOnOffStatus) {
//            hideProgressBar();
//            final GenericOnOffStatus status = (GenericOnOffStatus) meshMessage;
//            pam.add(status.getByte());
//
//            final boolean presentState = status.getPresentState();
//            final Boolean targetOnOff = status.getTargetState();
//            final int steps = status.getTransitionSteps();
//            final int resolution = status.getTransitionResolution();
//            if (targetOnOff == null) {
//                if (presentState) {
//                    onOffState.setText(R.string.generic_state_on);
//                    mActionOnOff.setText(R.string.action_generic_off);
//                } else {
//                    onOffState.setText(R.string.generic_state_off);
//                    mActionOnOff.setText(R.string.action_generic_on);
//                }
//                remainingTime.setVisibility(View.GONE);
//            } else {
//                if (!targetOnOff) {
//                    onOffState.setText(R.string.generic_state_on);
//                    mActionOnOff.setText(R.string.action_generic_off);
//                } else {
//                    onOffState.setText(R.string.generic_state_off);
//                    mActionOnOff.setText(R.string.action_generic_on);
//                }
//                remainingTime.setText(getString(R.string.remaining_time, MeshParserUtils.getRemainingTransitionTime(resolution, steps)));
//                remainingTime.setVisibility(View.VISIBLE);
//            }
//        }

        factoryNotify.updateProgressNotification(meshMessage.getSrc(), meshMessage.getDst(), meshMessage.getByte());
    }

    void handleTouchEvent(GenericOnOffStatus msg) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (msg.mTargetOn && (msg.isHOver || msg.isTaap)) {
                    int newdst = randomInt(NODES.size());
                    byte[] bb = getColorRandom();
                    Log.e("<<touch", msg.getSrc() + " -------- tid " + msg.getCustomTid());
                    if (elementss.get(newdst) == msg.getSrc()) {

                        sendColorTo(getElem2Node(msg.getSrc())
                                , GenericOnOffSet.LOCAL_START, optcode, bb);

                        Log.e("<<touch", elementss.get(newdst) + " open tid; " + msg.getCustomTid() + " on:" + msg.getParameters()[0] + " " + msg.getParameters()[1] + " " + msg.getParameters()[2]);
                        return;
                    } else {
                        int target = elementss.get(newdst);
//
                        sendTo(getElem2Node(msg.getSrc()), GenericOnOffSet.LOCAL_STOP, optcode);

                        Log.e("<<touch", msg.getSrc() + " close tid; " + msg.getCustomTid());
                        sendColorTo(getElem2Node(target)
                                , GenericOnOffSet.LOCAL_START, optcode, bb);

                        Log.e("<<touch", target + " open tid; " + msg.getCustomTid() + " on:" + msg.getParameters()[0] + " " + msg.getParameters()[1] + " " + msg.getParameters()[2]);
                    }
                }
            }
        });
        thread.start();
    }

    void handleTouchData(GenericOnOffStatus msg) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NodeStatus node = GenericOnOffStatus.statusMesh.get(msg.getSrc());
                node.setTid(msg.getCustomTid());
                node.setTap(msg.isTaap);
                node.setHover(msg.isHOver);
            }
        });
        thread.start();
    }

    Handler posHandle = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            GenericOnOffStatus node = (GenericOnOffStatus) msg.obj;
            final NodeStatus status = GenericOnOffStatus.statusMesh.get(node.getSrc());
            status.setTid(node.getCustomTid());
            status.setTap(node.isTaap || status.isTap());
            status.setHover(node.isHOver || status.isHover());
            Log.e("<<touch evnet", "update handle status" + Thread.currentThread().getName());
        }


    };

    Handler handlerSendMsg = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            for (int key : GenericOnOffStatus.statusMesh.keySet()) {
                NodeStatus node = GenericOnOffStatus.statusMesh.get(key);
                if (node.isOn() && (node.isHover() || node.isTap())) {

                }
            }
//            int newdst = randomInt(NODES.size());
//            byte[] bb = getColorRandom();
//            Log.e("<<touch", msg.getSrc() + " -------- tid " + msg.getCustomTid());
//            if (elementss.get(newdst) == msg.getSrc()) {
//
//                sendColorTo(getElem2Node(msg.getSrc())
//                        , GenericOnOffSet.LOCAL_START, optcode, bb);
//
//                Log.e("<<touch", elementss.get(newdst) + " open tid; " + msg.getCustomTid() + " on:" + msg.getParameters()[0] + " " + msg.getParameters()[1] + " " + msg.getParameters()[2]);
//                return;
//            } else {
//                int target = elementss.get(newdst);
////
//                sendTo(getElem2Node(msg.getSrc()), GenericOnOffSet.LOCAL_STOP, optcode);
//
//                Log.e("<<touch", msg.getSrc() + " close tid; " + msg.getCustomTid());
//                sendColorTo(getElem2Node(target)
//                        , GenericOnOffSet.LOCAL_START, optcode, bb);
//
//                Log.e("<<touch", target + " open tid; " + msg.getCustomTid() + " on:" + msg.getParameters()[0] + " " + msg.getParameters()[1] + " " + msg.getParameters()[2]);
//            }
        }
    };

    /**
     * Send generic on off get to mesh node
     */
    public void sendGenericOnOffGet() {
        if (!checkConnectivity()) return;
        final Element element = mViewModel.getSelectedElement().getValue();
        if (element != null) {
            final MeshModel model = mViewModel.getSelectedModel().getValue();
            if (model != null) {
                if (!model.getBoundAppKeyIndexes().isEmpty()) {
                    final int appKeyIndex = model.getBoundAppKeyIndexes().get(0);
                    final ApplicationKey appKey = mViewModel.getNetworkLiveData().getMeshNetwork().getAppKey(appKeyIndex);

                    final int address = element.getElementAddress();
                    Log.v(TAG, "Sending message to element's unicast address: " + MeshAddress.formatAddress(address, true));

                    final GenericOnOffGet genericOnOffSet = new GenericOnOffGet(appKey);
                    sendMessage(address, genericOnOffSet);
                } else {
                    mViewModel.displaySnackBar(this, mContainer, getString(R.string.error_no_app_keys_bound), Snackbar.LENGTH_LONG);
                }
            }
        }
    }

    /**
     * Send generic on off set to mesh node
     *
     * @param state true to turn on and false to turn off
     * @param delay message execution delay in 5ms steps. After this delay milliseconds the model will execute the required behaviour.
     */
    public void sendGenericOnOff(final boolean state, final Integer delay) {
        if (!checkConnectivity()) return;
        final ProvisionedMeshNode node = mViewModel.getSelectedMeshNode().getValue();
        if (node != null) {
            final Element element = mViewModel.getSelectedElement().getValue();
            if (element != null) {
                final MeshModel model = mViewModel.getSelectedModel().getValue();
                if (model != null) {
                    if (!model.getBoundAppKeyIndexes().isEmpty()) {
                        final int appKeyIndex = model.getBoundAppKeyIndexes().get(0);
                        final ApplicationKey appKey = mViewModel.getNetworkLiveData().getMeshNetwork().getAppKey(appKeyIndex);
                        final int address = element.getElementAddress();
                        final GenericOnOffSet genericOnOffSet = new GenericOnOffSet(appKey, 1,
                                node.getSequenceNumber(), 99, GenericOnOffSet.LOCAL_ASSIGN, 0);
                        sendMessage(address, genericOnOffSet);
                    } else {
                        mViewModel.displaySnackBar(this, mContainer, getString(R.string.error_no_app_keys_bound), Snackbar.LENGTH_LONG);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        elementss.clear();
    }
}
